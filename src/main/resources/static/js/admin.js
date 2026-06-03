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

        const filteredUsers = users.filter(u => !u.realmRoles?.includes('admin'));

        container.innerHTML = filteredUsers.map(u => {
            let currentRole = 'client'; 
            const roles = u.realmRoles || [];
            if (roles.includes('vendeur')) currentRole = 'vendeur';
            else if (roles.includes('livreur')) currentRole = 'livreur';

            return `
            <div class="user-card" style="border:1px solid #e2e8f0; padding:12px; margin-bottom:8px; border-radius:10px; background:white;">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <strong>👤 ${u.username || 'Sans nom'}</strong>
                    <select onchange="globalThis.changeUserRole('${u.id}', this.value)" style="font-size:11px; border-radius:4px;">
                        <option value="client" ${currentRole === 'client' ? 'selected' : ''}>🛒 Client</option>
                        <option value="vendeur" ${currentRole === 'vendeur' ? 'selected' : ''}>🏬 Vendeur</option>
                        <option value="livreur" ${currentRole === 'livreur' ? 'selected' : ''}>📦 Livreur</option>
                    </select>
                </div>
                <div style="margin-top:8px; display:flex; gap:5px;">
                    <button onclick="globalThis.toggleStatus('${u.id}', ${u.enabled})" class="btn-sm" 
                            style="background:${u.enabled ? '#f59e0b' : '#10b981'}; color:white; border:none; border-radius:4px; padding:4px 8px; cursor:pointer;">
                        ${u.enabled ? 'Bloquer' : 'Activer'}
                    </button>
                    <button onclick="globalThis.deleteUser('${u.id}')" class="btn-sm" style="background:#ef4444; color:white; border:none; border-radius:4px; padding:4px 8px; cursor:pointer;">🗑️</button>
                </div>
            </div>
        `;}).join('');
    } catch (e) {
        console.error("Erreur utilisateurs :", e);
    }
};

// ==========================================
// 3. GESTION DES CATÉGORIES (EXPLORATEUR)
// ==========================================

const updateBreadcrumb = () => {
    const nav = document.getElementById('category-breadcrumb');
    if (!nav) return;
    nav.innerHTML = categoryNavigationStack.map((c, i) => 
        `<span onclick="globalThis.navigateToBreadcrumb(${i})" style="cursor:pointer; color:var(--primary); font-weight:bold; text-decoration:underline;">${c.name}</span>`
    ).join(' <span style="color:#cbd5e1;">/</span> ');
};

const loadCategoriesDashboard = async () => {
    const list = document.getElementById('category-list');
    if (!list) return;
    updateBreadcrumb();
    try {
        const current = categoryNavigationStack.at(-1);
        // Si current.id est null, on demande les racines, sinon on demande les enfants
        const url = current.id ? `/api/admin/categories?parentId=${current.id}` : '/api/admin/categories';
        const categories = await globalThis.ApiService.fetch(url);
        
        if (!categories || categories.length === 0) {
            list.innerHTML = '<li style="padding:10px; color:gray; font-style:italic;">Dossier vide</li>';
            return;
        }

        list.innerHTML = categories.map(cat => `
            <li class="admin-item" style="display:flex; justify-content:space-between; align-items:center; padding:10px; border-bottom:1px solid #f1f5f9; ${selectedCategoryId == cat.id ? 'background:#eff6ff; border-left:4px solid #3b82f6;' : ''}">
                <span onclick="globalThis.selectCategory('${cat.id}', '${cat.name.replace(/'/g, "\\'")}')" style="cursor:pointer; flex:1; font-weight:600;">📁 ${cat.name}</span>
                <div style="display:flex; gap:5px;">
                    <button onclick="globalThis.diveIntoCategory('${cat.id}', '${cat.name.replace(/'/g, "\\'")}')" class="btn-sm" style="background:#f1f5f9; border:1px solid #cbd5e1; border-radius:4px; padding:2px 8px;">Ouvrir</button>
                    <button onclick="globalThis.deleteCategory('${cat.id}')" style="color:#ef4444; background:none; border:none; cursor:pointer; font-weight:bold;">✕</button>
                </div>
            </li>
        `).join('');
    } catch (e) {
        console.error("Erreur explorateur catégories :", e);
    }
};

globalThis.createCategory = async () => {
    const nameInput = document.getElementById('new-category-name');
    if (!nameInput?.value) return alert("Nom requis");

    const currentParent = categoryNavigationStack.at(-1);
    const payload = {
        name: nameInput.value,
        parentId: currentParent.id // null si on est à la racine
    };

    try {
        await globalThis.ApiService.post('/api/admin/categories', payload);
        nameInput.value = '';
        loadCategoriesDashboard();
    } catch (e) {
        console.error("Erreur création catégorie :", e);
    }
};

globalThis.deleteCategory = async (id) => {
    if (!confirm("Supprimer cette catégorie et ses sous-éléments ?")) return;
    try {
        await globalThis.ApiService.delete(`/api/admin/categories/${id}`);
        if (selectedCategoryId == id) selectedCategoryId = null;
        loadCategoriesDashboard();
    } catch (e) {
        console.error("Erreur suppression catégorie :", e);
        alert("Impossible de supprimer : vérifiez s'il y a des produits liés.");
    }
};

globalThis.diveIntoCategory = (id, name) => {
    categoryNavigationStack.push({ id: Number(id), name: name });
    loadCategoriesDashboard();
};

globalThis.navigateToBreadcrumb = (index) => {
    categoryNavigationStack = categoryNavigationStack.slice(0, index + 1);
    selectedCategoryId = null;
    loadCategoriesDashboard();
};

globalThis.selectCategory = (id, name) => {
    selectedCategoryId = id;
    const display = document.getElementById('active-cat-display');
    if(display) display.innerText = name;
    loadCategoriesDashboard(); // Pour mettre à jour l'effet visuel (bordure bleue)
    loadGlobalLibrary();
};

// ==========================================
// 4. BIBLIOTHÈQUE DE MODÈLES (CONTRAINTES)
// ==========================================

globalThis.createGlobalConstraint = async () => {
    const name = document.getElementById('lib-const-name');
    const type = document.getElementById('lib-const-type');

    if (!name?.value) return alert("Nom du modèle requis");

    try {
        await globalThis.ApiService.post('/api/admin/constraints', {
            name: name.value,
            controlType: type.value,
            required: false
        });
        name.value = '';
        loadGlobalLibrary();
    } catch (e) {
        console.error("Erreur création modèle :", e);
    }
};

const loadGlobalLibrary = async () => {
    const container = document.getElementById('global-constraints-library');
    if (!container) return;
    try {
        const allModels = await globalThis.ApiService.fetch('/api/admin/constraints');
        if (!allModels) return;

        let activeIds = [];
        if (selectedCategoryId) {
            const currentConstraints = await globalThis.ApiService.fetch(`/api/admin/categories/${selectedCategoryId}/constraints`);
            activeIds = currentConstraints ? currentConstraints.map(c => String(c.id)) : [];
        }

        container.innerHTML = allModels.map(m => `
            <div class="library-item" style="display:flex; align-items:center; gap:10px; padding:8px; border:1px solid #e2e8f0; margin-bottom:5px; border-radius:6px; background:white;">
                <input type="checkbox" ${activeIds.includes(String(m.id)) ? 'checked' : ''} onchange="globalThis.toggleLink('${m.id}', this.checked)">
                <span style="font-size:13px;">${m.name} <small style="color:gray;">(${m.controlType})</small></span>
                <button onclick="globalThis.deleteConstraint('${m.id}')" style="margin-left:auto; border:none; background:none; cursor:pointer; color:#94a3b8;">🗑️</button>
            </div>
        `).join('') || '<p style="font-size:12px; color:gray;">Aucun modèle.</p>';
    } catch (e) {
        console.error("Erreur bibliothèque :", e);
    }
};

globalThis.deleteConstraint = async (id) => {
    if(!confirm("Supprimer ce modèle de la bibliothèque ?")) return;
    try {
        await globalThis.ApiService.delete(`/api/admin/constraints/${id}`);
        loadGlobalLibrary();
    } catch (e) {
        console.error("Erreur suppression modèle :", e);
    }
};

globalThis.toggleLink = async (constraintId, isChecked) => {
    if(!selectedCategoryId) {
        alert("Cliquez sur le NOM d'une catégorie d'abord !");
        loadGlobalLibrary();
        return;
    }
    const method = isChecked ? 'POST' : 'DELETE';
    try {
        await globalThis.ApiService.fetch(`/api/admin/categories/${selectedCategoryId}/constraints/${constraintId}`, { method });
    } catch (e) {
        console.error("Erreur de liaison :", e);
        loadGlobalLibrary();
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