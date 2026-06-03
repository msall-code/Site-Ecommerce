// --- GESTION DE LA CRÉATION DE PRODUIT ---

// 1. Charger les catégories dans le select
async function loadCategoriesForForm() {
    const select = document.getElementById('p-category');
    if (!select) return;

    try {
        // On récupère toutes les catégories (incluant les flags)
        const categories = await globalThis.ApiService.fetch('/api/admin/categories');
        
        select.innerHTML = '<option value="">-- Sélectionner --</option>';
        categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.name;
            // On stocke les flags dans l'élément option pour y accéder facilement
            option.dataset.flags = JSON.stringify(cat);
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Erreur chargement catégories:", error);
    }
}

// 2. Détecter les besoins de la catégorie (Pointure, Genre...)
function onCategoryChange(categoryId) {
    const select = document.getElementById('p-category');
    const option = select.options[select.selectedIndex];
    const zone = document.getElementById('dynamic-zone');
    const container = document.getElementById('dynamic-inputs');
    
    if (!option.dataset.flags || !categoryId) {
        zone.style.display = "none";
        return;
    }

    const flags = JSON.parse(option.dataset.flags);
    container.innerHTML = "";
    let hasFields = false;

    if (flags.requiresGender) {
        hasFields = true;
        container.innerHTML += `
            <div class="form-group">
                <label>Genre</label>
                <select id="p-gender" class="form-control">
                    <option value="M">Masculin</option>
                    <option value="F">Féminin</option>
                    <option value="U">Unisexe</option>
                </select>
            </div>`;
    }

    if (flags.requiresShoeSize) {
        hasFields = true;
        container.innerHTML += `
            <div class="form-group">
                <label>Pointure</label>
                <input type="number" id="p-shoeSize" class="form-control" placeholder="Ex: 42">
            </div>`;
    }

    if (flags.requiresSizeText) {
        hasFields = true;
        container.innerHTML += `
            <div class="form-group">
                <label>Taille (S, M, L...)</label>
                <input type="text" id="p-sizeText" class="form-control" placeholder="Ex: XL">
            </div>`;
    }

    zone.style.display = hasFields ? "block" : "none";
}

// 3. Envoyer le produit au Backend
// Remplace la fonction handleCreateProduct par celle-ci pour corriger les erreurs Sonar
async function handleCreateProduct(event) {
    event.preventDefault();

    const payload = {
        name: document.getElementById('p-name').value,
        description: document.getElementById('p-desc').value,
        // S7773: Utilisation de Number.parseFloat
        price: Number.parseFloat(document.getElementById('p-price').value),
        // S7773: Utilisation de Number.parseInt
        stock: Number.parseInt(document.getElementById('p-stock').value, 10),
        imageUrl: document.getElementById('p-image').value,
        categoryId: document.getElementById('p-category').value,
        
        gender: document.getElementById('p-gender')?.value || null,
        // S7773: Utilisation de Number.parseInt ici aussi
        shoeSize: document.getElementById('p-shoeSize')?.value ? Number.parseInt(document.getElementById('p-shoeSize').value, 10) : null,
        sizeText: document.getElementById('p-sizeText')?.value || null,
        
        latitude: 14.7167,
        longitude: -17.4677
    };

    try {
        // S1481 & S1854: Suppression de 'const response =' car la variable n'est pas utilisée
        await globalThis.ApiService.post('/api/products/add', payload);
        alert("🎉 Produit ajouté ! Scan prêt.");
        // S7764: Utilisation de globalThis au lieu de window
        globalThis.location.href = "gestion-boutique.html";
    } catch (error) {
        console.error("Erreur création produit:", error);
        alert("Erreur: " + (error.message || "Impossible de créer le produit"));
    }
}

// Exports pour le HTML
globalThis.loadCategoriesForForm = loadCategoriesForForm;
globalThis.onCategoryChange = onCategoryChange;
globalThis.handleCreateProduct = handleCreateProduct;