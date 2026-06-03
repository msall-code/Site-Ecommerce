// ==========================================
// 1. ÉTATS ET VARIABLES GLOBALES
// ==========================================
let categoryNavigationStack = [{ id: null, name: "Accueil" }];
let selectedCategoryId = null;

// ==========================================
// 2. GESTION DES UTILISATEURS (KEYCLOAK)
// ==========================================

const loadUsersDashboard = async () => {
    const container = document.getElementById('user-list');
    if (!container) return;
    try {
        const users = await globalThis.ApiService.fetch('/api/admin/users');
        if (!users) return;

        // On masque les admins de la liste pour la gestion métier
        const filteredUsers = users.filter(u => !u.realmRoles?.includes('admin'));

        container.innerHTML = filteredUsers.map(u => {
            // Détection du rôle actuel (SonarLint S6582 corrigé avec optional chaining)
            let currentRole = 'client'; 
            const roles = u.realmRoles;
            if (roles?.includes('vendeur')) currentRole = 'vendeur';
            else if (roles?.includes('livreur')) currentRole = 'livreur';

            return `
            <div class="user-card" style="border:1px solid #e2e8f0; padding:12px; margin-bottom:8px; border-radius:10px; background:white;">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <div style="display:flex; flex-direction:column;">
                        <strong>👤 ${u.username || 'Sans nom'}</strong>
                        <small style="color:gray;">${u.email || ''}</small>
                    </div>
                    <select onchange="globalThis.changeUserRole('${u.id}', this.value)" 
                            style="font-size:12px; border:1px solid #cbd5e1; border-radius:6px; padding:4px;">
                        <option value="client" ${currentRole === 'client' ? 'selected' : ''}>🛒 Client</option>
                        <option value="vendeur" ${currentRole === 'vendeur' ? 'selected' : ''}>🏬 Vendeur</option>
                        <option value="livreur" ${currentRole === 'livreur' ? 'selected' : ''}>📦 Livreur</option>
                    </select>
                </div>
                <div style="margin-top:12px; display:flex; gap:8px; border-top:1px solid #f1f5f9; padding-top:8px;">
                    <button onclick="globalThis.toggleStatus('${u.id}', ${u.enabled})" class="btn-sm" 
                            style="flex:1; background:${u.enabled ? '#f59e0b' : '#10b981'}; color:white; border:none; border-radius:6px; padding:6px; cursor:pointer;">
                        ${u.enabled ? '🚫 Bloquer' : '✅ Activer'}
                    </button>
                    <button onclick="globalThis.deleteUser('${u.id}')" class="btn-sm" 
                            style="background:#fee2e2; color:#ef4444; border:none; border-radius:6px; padding:6px 10px; cursor:pointer;">🗑️</button>
                </div>
            </div>
        `;}).join('');
    } catch (e) {
        console.error("Erreur chargement utilisateurs :", e);
    }
};

globalThis.changeUserRole = async (userId, newRole) => {
    try {
        await globalThis.ApiService.post(`/api/admin/users/${userId}/role`, { role: newRole });
        loadUsersDashboard();
    } catch (e) {
        console.error("Erreur changement rôle :", e);
    }
};

globalThis.toggleStatus = async (userId, currentStatus) => {
    try {
        await globalThis.ApiService.post(`/api/admin/users/${userId}/status`, { enabled: !currentStatus });
        loadUsersDashboard();
    } catch (e) {
        console.error("Erreur changement statut :", e);
    }
};

globalThis.deleteUser = async (userId) => {
    if (!confirm("Supprimer cet utilisateur ?")) return;
    try {
        await globalThis.ApiService.delete(`/api/admin/users/${userId}`);
        loadUsersDashboard();
    } catch (e) {
        console.error("Erreur suppression utilisateur :", e);
    }
};

// ==========================================
// 3. GESTION DES CATÉGORIES
// ==========================================

const updateBreadcrumb = () => {
    const nav = document.getElementById('category-breadcrumb');
    if (!nav) return;
    nav.innerHTML = categoryNavigationStack.map((c, i) => 
        `<span onclick="globalThis.navigateToBreadcrumb(${i})" style="cursor:pointer; color:var(--primary); text-decoration:underline;">${c.name}</span>`
    ).join(' / ');
};

const loadCategoriesDashboard = async () => {
    const list = document.getElementById('category-list');
    if (!list) return;
    updateBreadcrumb();
    try {
        const current = categoryNavigationStack.at(-1);
        const url = current?.id ? `/api/admin/categories?parentId=${current.id}` : '/api/admin/categories';
        const categories = await globalThis.ApiService.fetch(url);
        if (!categories) return;

        list.innerHTML = categories.map(cat => `
            <li class="admin-item" style="display:flex; justify-content:space-between; padding:10px; border-bottom:1px solid #f1f5f9;">
                <span onclick="globalThis.selectCategory('${cat.id}', '${cat.name}')" style="cursor:pointer; font-weight:600;">📁 ${cat.name}</span>
                <button onclick="globalThis.diveIntoCategory('${cat.id}', '${cat.name}')" class="btn-sm">Ouvrir</button>
            </li>
        `).join('');
    } catch (e) {
        console.error("Erreur chargement catégories :", e);
    }
};

globalThis.diveIntoCategory = (id, name) => {
    categoryNavigationStack.push({ id: Number(id), name: name });
    loadCategoriesDashboard();
};

globalThis.navigateToBreadcrumb = (index) => {
    categoryNavigationStack = categoryNavigationStack.slice(0, index + 1);
    loadCategoriesDashboard();
};

globalThis.selectCategory = (id, name) => {
    selectedCategoryId = id;
    const display = document.getElementById('active-cat-display');
    if(display) display.innerText = name;
    loadGlobalLibrary();
};

// ==========================================
// 4. BIBLIOTHÈQUE DE CONTRAINTES
// ==========================================

globalThis.createGlobalConstraint = async () => {
    const nameEl = document.getElementById('lib-const-name');
    const typeEl = document.getElementById('lib-const-type');
    if (!nameEl?.value) return alert("Nom requis");

    try {
        await globalThis.ApiService.post('/api/admin/constraints', {
            name: nameEl.value,
            controlType: typeEl.value,
            required: false
        });
        nameEl.value = '';
        await loadGlobalLibrary(); 
    } catch (e) {
        console.error("Erreur création contrainte :", e);
    }
};

const loadGlobalLibrary = async () => {
    const container = document.getElementById('global-constraints-library');
    if (!container) return;

    try {
        const allModels = await globalThis.ApiService.fetch('/api/admin/constraints');
        if (!Array.isArray(allModels)) return;

        let activeIds = [];
        if (selectedCategoryId) {
            try {
                const current = await globalThis.ApiService.fetch(`/api/admin/categories/${selectedCategoryId}/constraints`);
                activeIds = current ? current.map(c => c.id) : [];
            } catch (err) {
                console.error("Erreur lors du mappage des liaisons :", err);
            }
        }

        container.innerHTML = allModels.map(m => `
            <div class="library-item" style="display:flex; align-items:center; gap:10px; padding:8px; border:1px solid #e2e8f0; margin-bottom:5px; border-radius:6px; background:white;">
                <input type="checkbox" ${activeIds.includes(m.id) ? 'checked' : ''} onchange="globalThis.toggleLink('${m.id}', this.checked)">
                <span style="font-size:13px;">${m.name} <small>(${m.controlType})</small></span>
            </div>
        `).join('');
    } catch (e) {
        console.error("Erreur bibliothèque graphique :", e);
    }
};

globalThis.toggleLink = async (constraintId, isChecked) => {
    if(!selectedCategoryId) return alert("Sélectionnez une catégorie !");
    const method = isChecked ? 'POST' : 'DELETE';
    try {
        await globalThis.ApiService.fetch(`/api/admin/categories/${selectedCategoryId}/constraints/${constraintId}`, { method });
    } catch (e) {
        console.error("Erreur de liaison :", e);
    }
};

// ==========================================
// 5. INITIALISATION
// ==========================================

document.addEventListener("DOMContentLoaded", () => {
    loadCategoriesDashboard();
    loadUsersDashboard();
    loadGlobalLibrary();
});