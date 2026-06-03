// ==========================================
// 1. ÉTATS ET VARIABLES GLOBALES
// ==========================================
let categoryNavigationStack = [{ id: null, name: "Accueil" }];
let selectedCategoryId = null;

// ==========================================
// 2. DÉFINITION DES FONCTIONS
// ==========================================

// Met à jour le fil d'Ariane (Breadcrumb)
const updateBreadcrumb = () => {
    const nav = document.getElementById('category-breadcrumb');
    if (!nav) return;
    nav.innerHTML = categoryNavigationStack.map((c, i) => 
        `<span onclick="globalThis.navigateToBreadcrumb(${i})" style="cursor:pointer; color:var(--primary); font-weight:bold; text-decoration:underline;">${c.name}</span>`
    ).join(' <span style="color:#cbd5e1;">/</span> ');
};

// Charge la liste des utilisateurs
const loadUsersDashboard = async () => {
    const container = document.getElementById('user-list');
    if (!container) return;
    try {
        const users = await globalThis.ApiService.fetch('/api/admin/users');
        container.innerHTML = users.map(u => `
            <div class="user-card" style="border:1px solid #e2e8f0; padding:12px; margin-bottom:8px; border-radius:10px; background:white;">
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <strong>👤 ${u.username || 'Sans nom'}</strong>
                    <span style="font-size:10px; color:var(--text-muted);">${u.role}</span>
                </div>
                <div style="margin-top:8px; display:flex; gap:5px;">
                    <button onclick="globalThis.toggleStatus('${u.id}')" class="btn-sm" style="background:${u.active ? '#f59e0b' : '#10b981'}; color:white; border:none; border-radius:4px; padding:4px 8px; cursor:pointer;">
                        ${u.active ? 'Bloquer' : 'Activer'}
                    </button>
                    <button onclick="globalThis.deleteUser('${u.id}')" class="btn-sm" style="background:#ef4444; color:white; border:none; border-radius:4px; padding:4px 8px; cursor:pointer;">🗑️</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        console.error("Erreur lors du chargement des utilisateurs :", e);
    }
};

// Charge les catégories
const loadCategoriesDashboard = async () => {
    const list = document.getElementById('category-list');
    if (!list) return;
    updateBreadcrumb();
    try {
        const current = categoryNavigationStack.at(-1);
        const url = current.id ? `/api/admin/categories?parentId=${current.id}` : '/api/admin/categories';
        const categories = await globalThis.ApiService.fetch(url);
        
        list.innerHTML = categories.map(cat => `
            <li class="admin-item" style="display:flex; justify-content:space-between; align-items:center; padding:10px; border-bottom:1px solid #f1f5f9; ${selectedCategoryId == cat.id ? 'background:#eff6ff;' : ''}">
                <span onclick="globalThis.selectCategory('${cat.id}', '${cat.name}')" style="cursor:pointer; flex:1; font-weight:600;">📁 ${cat.name}</span>
                <div style="display:flex; gap:5px;">
                    <button onclick="globalThis.diveIntoCategory('${cat.id}', '${cat.name}')" style="font-size:10px; padding:2px 6px;">Ouvrir</button>
                    <button onclick="globalThis.deleteCategory('${cat.id}')" style="color:red; background:none; border:none; cursor:pointer;">✕</button>
                </div>
            </li>
        `).join('');
    } catch (e) {
        console.error("Erreur lors du chargement des catégories :", e);
    }
};

// Charge la bibliothèque de contraintes
const loadGlobalLibrary = async () => {
    const container = document.getElementById('global-constraints-library');
    if (!container) return;
    try {
        // NOTE: On utilise /api/admin/constraints (vérifie que ton GET Java est bien sur cette route)
        const allModels = await globalThis.ApiService.fetch('/api/admin/constraints');
        
        let activeIds = [];
        if (selectedCategoryId) {
            const currentConstraints = await globalThis.ApiService.fetch(`/api/admin/categories/${selectedCategoryId}/constraints`);
            activeIds = currentConstraints.map(c => c.id);
        }

        container.innerHTML = allModels.map(m => `
            <div class="library-item" style="display:flex; align-items:center; gap:10px; padding:8px; border:1px solid #e2e8f0; margin-bottom:5px; border-radius:6px; background:white;">
                <input type="checkbox" ${activeIds.includes(m.id) ? 'checked' : ''} onchange="globalThis.toggleLink('${m.id}', this.checked)">
                <span style="font-size:13px; color:#1e293b;">${m.name} <small>(${m.controlType})</small></span>
            </div>
        `).join('');
    } catch (e) {
        console.error("Erreur bibliothèque :", e);
    }
};

// Actions de navigation et liaison
const diveIntoCategory = (id, name) => {
    categoryNavigationStack.push({ id: Number(id), name: name });
    loadCategoriesDashboard();
};

const navigateToBreadcrumb = (index) => {
    categoryNavigationStack = categoryNavigationStack.slice(0, index + 1);
    loadCategoriesDashboard();
};

const selectCategory = (id, name) => {
    selectedCategoryId = id;
    const display = document.getElementById('active-cat-display');
    if(display) display.innerText = name;
    loadCategoriesDashboard();
    loadGlobalLibrary();
};

const toggleLink = async (constraintId, isChecked) => {
    if(!selectedCategoryId) return alert("Sélectionnez une catégorie à gauche !");
    const method = isChecked ? 'POST' : 'DELETE';
    try {
        await globalThis.ApiService.fetch(`/api/admin/categories/${selectedCategoryId}/constraints/${constraintId}`, { method });
    } catch (e) {
        console.error("Erreur de liaison :", e);
    }
};

// ==========================================
// 3. EXPORTATION DANS GLOBALTHIS
// ==========================================
// On attache TOUT pour que le HTML puisse voir les fonctions
globalThis.updateBreadcrumb = updateBreadcrumb;
globalThis.loadUsersDashboard = loadUsersDashboard;
globalThis.loadCategoriesDashboard = loadCategoriesDashboard;
globalThis.loadGlobalLibrary = loadGlobalLibrary;
globalThis.diveIntoCategory = diveIntoCategory;
globalThis.navigateToBreadcrumb = navigateToBreadcrumb;
globalThis.selectCategory = selectCategory;
globalThis.toggleLink = toggleLink;

// Action utilisateur supplémentaire
globalThis.toggleStatus = async (userId) => {
    try {
        await globalThis.ApiService.post(`/api/admin/users/${userId}/toggle-status`);
        loadUsersDashboard();
    } catch (e) { console.error(e); }
};

// ==========================================
// 4. LANCEMENT AU CHARGEMENT
// ==========================================
document.addEventListener("DOMContentLoaded", () => {
    // Maintenant que tout est défini et exporté, on peut appeler
    globalThis.loadCategoriesDashboard();
    globalThis.loadUsersDashboard();
    globalThis.loadGlobalLibrary();
});