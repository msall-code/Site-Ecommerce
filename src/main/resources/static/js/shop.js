    async function loadMyProducts() {
        const container = document.getElementById('product-list');
        if (!container) return;
        
        try {
            const products = await globalThis.ApiService.fetch('/api/products/my-products');
            
            if (!products || products.length === 0) {
                container.innerHTML = `<p style="text-align:center;">Aucun article en vente.</p>`;
                return;
            }

            container.innerHTML = products.map(p => `
                <div class="product-card">
                    <div class="product-info">
                        <span class="product-badge">${p.category || 'Général'}</span>
                        <h3>${p.name}</h3>
                        <div class="product-price">${p.price} FCFA</div>
                        ${p.gender ? `<small>Genre: ${p.gender}</small>` : ''}
                    </div>
                    
                    ${p.qrCodeData ? `
                        <div class="qr-section" style="text-align:center; margin-top:10px; border-top:1px dashed #ccc; padding-top:10px;">
                            <small>QR Code Logistique :</small>
                            <div style="font-family:monospace; font-size:10px; background:#eee; padding:5px;">${p.qrCodeData}</div>
                        </div>
                    ` : ''}
                </div>
            `).join('');

        } catch (error) {
            container.innerHTML = `<div class="error">Erreur de chargement.</div>`;
        }
    }
    globalThis.loadMyProducts = loadMyProducts;