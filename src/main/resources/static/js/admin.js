// Historique de navigation pour l'explorateur (Pile / Stack)
let categoryNavigationStack = [{ id: null, name: "Accueil (Catégories Mères)" }];

function getCurrentPath() {
    return categoryNavigationStack.at(-1);
}

async function loadCategoriesDashboard() {
    const listContainer = document.getElementById('category-list');
    const pathContainer = document.getElementById('category-breadcrumb');
    const flagsSection = document.getElementById('root-category-flags');
    
    if (!listContainer) return;

    const currentLevel = getCurrentPath();

    if (pathContainer) {
        pathContainer.innerHTML = categoryNavigationStack.map((node, index) => `
            <span onclick="globalThis.navigateToBreadcrumb(${index})" style="color: #0284c7; cursor: pointer; font-weight: 600; font-size: 13px;">
                ${node.name}
            </span>
        `).join(' <span style="color:#9ca3af; padding: 0 4px;">&gt;</span> ');
    }

    // CORRECTION : On affiche toujours la section des flags pour pouvoir spécialiser les sous-catégories
    if (flagsSection) {
        flagsSection.style.display = "flex"; 
    }

    try {
        const url = currentLevel.id ? `/api/admin/categories?parentId=${currentLevel.id}` : '/api/admin/categories';
        const categories = await globalThis.ApiService.fetch(url);
        
        if (!categories || categories.length === 0) {
            listContainer.innerHTML = `<li style="padding:20px; text-align:center; color:#6b7280; list-style:none;">Aucun sous-ensemble trouvé dans cette section.</li>`;
        } else {
            listContainer.innerHTML = categories.map(cat => {
                const indicators = [];
                if (cat.requiresGender) indicators.push("🚻 Genre");
                if (cat.requiresSizeText) indicators.push("📏 Tailles S-XL");
                if (cat.requiresShoeSize) indicators.push("👟 Pointures");
                if (cat.requiresPrescription) indicators.push("📄 Ordonnance Requise");
                // On vérifie les deux noms possibles suite aux corrections DTO/Lombok
                if (cat.isFoodDelivery || cat.foodDelivery) indicators.push("🛵 Restauration Rapide");
                if (cat.requiresColdChain) indicators.push("❄️ Chaîne Froide");
                if (cat.isAgeRestricted || cat.ageRestricted) indicators.push("🔞 +18 ans");
                if (cat.isSoldByWeight || cat.soldByWeight) indicators.push("⚖️ Au poids");

                const badgesHtml = indicators.map(i => `
                    <span style="font-size:10px; background:#f1f5f9; padding:2px 6px; border-radius:4px; color:#475569; font-weight:500;">${i}</span>
                `).join('');

                const badgeSpecs = indicators.length > 0 
                    ? `<div style="display:flex; gap:4px; margin-top:4px; flex-wrap:wrap;">${badgesHtml}</div>`
                    : '';

                return `
                    <li style="padding: 14px; border-bottom: 1px solid #f3f4f6; background: #ffffff; margin-bottom: 6px; border-radius: 8px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 1px 3px rgba(0,0,0,0.02);">
                        <div>
                            <strong style="font-size: 14px; color:#1f2937;">📁 ${cat.name}</strong>
                            ${badgeSpecs}
                        </div>
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <button onclick="globalThis.diveIntoCategory('${cat.id}', '${cat.name}')" style="background: #e0f2fe; border: none; color: #0369a1; padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight:600; cursor: pointer;">
                                ⚙️ Gérer les sous-ensembles
                            </button>
                            <button onclick="globalThis.deleteCategory('${cat.id}')" style="background: none; border: none; color: #ef4444; font-size: 14px; cursor: pointer; padding: 0 4px;">
                                🗑️
                            </button>
                        </div>
                    </li>
                `;
            }).join('');
        }
    } catch (error) {
        console.error("Erreur catégories :", error);
        listContainer.innerHTML = "<li style='color: var(--danger); padding: 10px;'>Erreur lors de la récupération.</li>";
    }
}

function diveIntoCategory(id, name) {
    categoryNavigationStack.push({ id: Number.parseInt(id, 10), name: name });
    loadCategoriesDashboard();
}

function navigateToBreadcrumb(index) {
    categoryNavigationStack = categoryNavigationStack.slice(0, index + 1);
    loadCategoriesDashboard();
}

async function addCategory() {
    const inputElement = document.getElementById('newCatName');
    if (!inputElement?.value.trim()) return;

    const currentLevel = getCurrentPath();
    
    // CORRECTION : On envoie les flags sélectionnés quel que soit le niveau (parentId)
    const payload = {
        name: inputElement.value.trim(),
        parentId: currentLevel.id,
        requiresGender: document.getElementById('flagGender').checked,
        requiresSizeText: document.getElementById('flagSizeText').checked,
        requiresShoeSize: document.getElementById('flagShoeSize').checked,
        requiresPrescription: document.getElementById('flagPrescription').checked,
        isFoodDelivery: document.getElementById('flagFood').checked
    };

    try {
        await globalThis.ApiService.post('/api/admin/categories', payload);
        inputElement.value = "";
        
        // Reset des checkboxes après l'ajout réussi
        document.getElementById('flagGender').checked = false;
        document.getElementById('flagSizeText').checked = false;
        document.getElementById('flagShoeSize').checked = false;
        document.getElementById('flagPrescription').checked = false;
        document.getElementById('flagFood').checked = false;

        await loadCategoriesDashboard();
    } catch (error) {
        console.error("Erreur lors de l'ajout :", error);
        alert("Action impossible.");
    }
}

// ... Le reste des fonctions (deleteCategory, loadUsers, etc.) reste identique ...

async function deleteCategory(id) {
    if (!confirm("Supprimer cet élément et ses sous-catégories ?")) return;
    try {
        await globalThis.ApiService.delete(`/api/admin/categories/${id}`);
        await loadCategoriesDashboard();
    } catch (error) {
        console.error("Erreur suppression :", error);
    }
}

async function loadUsers() {
    const userContainer = document.getElementById('user-list');
    if (!userContainer) return;

    try {
        const users = await globalThis.ApiService.fetch('/api/admin/users');
        if (!users || users.length === 0) {
            userContainer.innerHTML = "<p class='loading-text'>Aucun utilisateur inscrit.</p>";
            return;
        }

        userContainer.innerHTML = users.map(user => {
            const activeRoles = user.realmRoles || [];
            let userRole = 'client'; 
            
            if (activeRoles.includes('admin')) userRole = 'admin';
            else if (activeRoles.includes('vendeur')) userRole = 'vendeur';
            else if (activeRoles.includes('livreur')) userRole = 'livreur';

            const roleColors = { 
                admin: "var(--danger)", 
                vendeur: "var(--secondary)", 
                livreur: "#10b981", 
                client: "var(--primary-light)" 
            };
            const roleBadgeColor = roleColors[userRole] || "#6b7280";

            const isBlocked = user.enabled === false;
            const statusLabel = isBlocked ? "🚫 Bloqué" : "✅ Actif";
            const blockButtonText = isBlocked ? "🔓 Débloquer" : "🚫 Bloquer";
            const isDisabledIfAdmin = userRole === 'admin' ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : '';

            return `
                <div style="padding: 12px; background: #f9fafb; border-radius: 8px; margin-bottom: 10px; display: flex; flex-direction: column; gap: 10px; border-left: 4px solid ${roleBadgeColor};">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <strong style="font-size: 14px;">${user.username} ${isBlocked ? '<span style="color:var(--danger); font-size:11px;">(Bloqué)</span>' : ''}</strong>
                            <span style="font-size: 12px; color: var(--text-muted); display: block;">${user.email || 'Pas d\'email'}</span>
                        </div>
                        <div style="display:flex; gap: 5px; align-items: center;">
                            <span style="font-size: 10px; padding: 2px 6px; background: #eee; border-radius: 4px;">${statusLabel}</span>
                            <span style="font-size: 11px; font-weight: bold; text-transform: uppercase; background: #f3f4f6; padding: 4px 8px; border-radius: 4px; color: ${roleBadgeColor};">
                                ${userRole}
                            </span>
                        </div>
                    </div>
                    
                    <div style="display: flex; gap: 8px; justify-content: flex-end; border-top: 1px solid #f1f5f9; padding-top: 8px; flex-wrap: wrap;">
                        <select ${isDisabledIfAdmin} onchange="globalThis.changeUserRole('${user.id}', this.value)" style="font-size: 12px; padding: 4px 8px; border-radius: 4px; border: 1px solid #cbd5e1; background: white;">
                            <option value="">Changer rôle...</option>
                            <option value="client" ${userRole === 'client' ? 'selected' : ''}>Client</option>
                            <option value="vendeur" ${userRole === 'vendeur' ? 'selected' : ''}>Vendeur</option>
                            <option value="livreur" ${userRole === 'livreur' ? 'selected' : ''}>Livreur</option>
                        </select>

                        <button ${isDisabledIfAdmin} onclick="globalThis.toggleBlockUser('${user.id}', ${user.enabled})" style="background: none; border: none; color: #3b82f6; font-size: 12px; cursor: pointer; font-weight: 600;">
                            ${blockButtonText}
                        </button>

                        <button ${isDisabledIfAdmin} onclick="globalThis.deleteUser('${user.id}')" style="background: none; border: none; color: var(--danger); font-size: 12px; cursor: pointer; font-weight: 600;">
                            🗑️ Supprimer
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error("Erreur utilisateurs :", error);
    }
}

async function toggleBlockUser(userId, currentEnabledStatus) {
    const newState = !currentEnabledStatus;
    const action = newState ? "débloquer" : "bloquer";
    if (!confirm(`Confirmer : ${action} l'utilisateur ?`)) return;
    try {
        await globalThis.ApiService.post(`/api/admin/users/${userId}/status`, { enabled: newState });
        await loadUsers();
    } catch (error) { 
        console.error("Erreur statut :", error);
    }
}

async function changeUserRole(userId, newRole) {
    if (!newRole || !confirm(`Changer le rôle en ${newRole} ?`)) return;
    try {
        await globalThis.ApiService.post(`/api/admin/users/${userId}/role`, { role: newRole });
        await loadUsers();
    } catch (error) { 
        console.error("Erreur modification rôle :", error);
    }
}

async function deleteUser(userId) {
    if (!confirm("Supprimer cet utilisateur ?")) return;
    try {
        await globalThis.ApiService.post(`/api/admin/users/${userId}/delete`, {});
        await loadUsers();
    } catch (error) { 
        console.error("Erreur suppression utilisateur :", error);
    }
}

// Exports globaux
globalThis.loadCategoriesDashboard = loadCategoriesDashboard;
globalThis.diveIntoCategory = diveIntoCategory;
globalThis.navigateToBreadcrumb = navigateToBreadcrumb;
globalThis.addCategory = addCategory;
globalThis.deleteCategory = deleteCategory;
globalThis.loadUsers = loadUsers;
globalThis.changeUserRole = changeUserRole;
globalThis.toggleBlockUser = toggleBlockUser;
globalThis.deleteUser = deleteUser;