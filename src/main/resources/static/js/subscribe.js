// js/subscribe.js

async function handleSubscription(event) {
    event.preventDefault();

    // Récupération du rôle
    const roleSelect = document.getElementById('role');
    const roleRaw = roleSelect?.value || 'client';
    
    // Construction de l'objet de données avec sécurités (?.value)
    const subscribeData = {
        username: document.getElementById('username').value.trim(),
        email: document.getElementById('email').value.trim(),
        password: document.getElementById('password').value,
        firstName: document.getElementById('firstName')?.value.trim() || "", 
        lastName: document.getElementById('lastName')?.value.trim() || "",  
        role: roleRaw.toUpperCase(), 
        // On utilise l'optionnel chaining ?. pour éviter le crash si l'élément est absent
        storeName: roleRaw === 'vendeur' ? (document.getElementById('storeName')?.value || null) : null,
        address: roleRaw === 'vendeur' ? (document.getElementById('address')?.value || null) : null,
        vehicle: roleRaw === 'livreur' ? (document.getElementById('vehicle')?.value || null) : null
    };

    console.log("JSON envoyé au Backend :", subscribeData);

    try {
        const response = await fetch('http://localhost:8099/api/auth/subscribe', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(subscribeData)
        });

        const responseText = await response.text();

        if (response.ok) {
            alert("Compte créé avec succès !");
            window.location.href = 'index.html'; 
        } else {
            console.error("Détails de l'erreur Backend :", responseText);
            alert("Erreur lors de l'inscription : " + responseText); 
        }
    } catch (error) {
        console.error("Erreur réseau :", error);
        alert("Impossible de joindre le serveur. Vérifie ta connexion.");
    }
}

// Gestion de l'affichage dynamique des champs selon le rôle
function toggleFields() {
    const roleSelect = document.getElementById('role');
    const vendeurFields = document.getElementById('vendeur-fields');
    const livreurFields = document.getElementById('livreur-fields');

    if (!roleSelect) return;
    const role = roleSelect.value;

    if (vendeurFields) {
        vendeurFields.style.display = (role === 'vendeur') ? 'block' : 'none';
    }
    if (livreurFields) {
        livreurFields.style.display = (role === 'livreur') ? 'block' : 'none';
    }
}

// Exportation pour le HTML
globalThis.handleSubscription = handleSubscription;
globalThis.toggleFields = toggleFields;