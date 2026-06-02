// js/catalogue.js

// js/catalogue.js

async function loadCatalogue() {
    const container = document.getElementById('catalogue-grid');
    if (!container) return;

    try {
        // APPEL DU CATALOGUE DE PRODUITS (et non du blog)
        const products = await globalThis.ApiService.fetch('/api/products/catalog');
        
        if (!products || products.length === 0) {
            container.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 40px 0;">
                    <p>📦 Aucun article disponible dans le catalogue pour le moment.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = products.map(prod => {
            // Gestion de l'image par défaut
            const img = prod.imageUrl || 'https://via.placeholder.com/300x200?text=Pas+d+image';
            
            return `
                <div class="product-card">
                    <div style="height: 150px; overflow: hidden; border-radius: 8px; margin-bottom: 10px;">
                        <img src="${img}" alt="${prod.name}" style="width: 100%; height: 100%; object-fit: cover;">
                    </div>
                    <div class="product-info">
                        <span class="product-badge" style="background: #e0f2fe; color: #0369a1;">${prod.category || 'Général'}</span>
                        <h3 style="margin: 10px 0 5px 0; font-size: 18px;">${prod.name}</h3>
                        <p class="price">${prod.price.toFixed(2)} FCFA</p>
                        <p class="seller">🏪 Boutique : ${prod.storeName || 'Inconnue'}</p>
                        
                        <div style="font-size: 11px; color: var(--secondary); margin-top: 5px;">
                            ${prod.gender ? `🚻 ${prod.gender}` : ''} 
                            ${prod.shoeSize ? `👟 T. ${prod.shoeSize}` : ''}
                            ${prod.sizeText ? `📏 ${prod.sizeText}` : ''}
                        </div>
                    </div>
                    <button onclick="globalThis.addToCart(${prod.id})" class="btn-primary btn-full" style="margin-top: 15px;">
                        Ajouter au panier 🛒
                    </button>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error("Erreur catalogue:", error);
        container.innerHTML = `<div class="error">Impossible de charger le catalogue.</div>`;
    }
}

function addToCart(id) {
    console.log("Ajout au panier du produit :", id);
    alert("Produit ajouté ! (Fonctionnalité panier à venir)");
}

globalThis.loadCatalogue = loadCatalogue;
globalThis.addToCart = addToCart;