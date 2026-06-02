// js/subscribe.js

async function handleSubscription(event) {
    event.preventDefault();

    // Récupération des éléments du DOM
    const roleSelect = document.getElementById('role');
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const storeNameInput = document.getElementById('storeName');
    const addressInput = document.getElementById('address');
    const vehicleInput = document.getElementById('vehicle');

    const role = roleSelect ? roleSelect.value : 'client';
    console.log("Rôle sélectionné pour l'inscription :", role);

    // Construction du payload attendu par ton RegistrationController Spring Boot
    const subscribeData = {
        username: usernameInput ? usernameInput.value : '',
        email: emailInput ? emailInput.value : '',
        password: passwordInput ? passwordInput.value : '',
        role: role,
        storeName: role === 'vendeur' && storeNameInput ? storeNameInput.value : null,
        address: role === 'vendeur' && addressInput ? addressInput.value : null,
        vehicle: role === 'livreur' && vehicleInput ? vehicleInput.value : null
    };

    try {
        // Envoi vers ton API Spring Boot locale (port 8099)
        const response = await fetch('http://localhost:8099/api/auth/subscribe', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(subscribeData)
        });

        if (response.ok) {
            alert("Compte créé avec succès ! Connectez-vous.");
            globalThis.location.href = 'login.html'; // Redirection vers la page de connexion
        } else {
            const errorText = await response.text();
            alert("Erreur lors de l'inscription : " + errorText);
        }
    } catch (error) {
        console.error("Erreur d'inscription:", error);
        alert("Impossible de joindre le serveur de l'API. Vérifie que Spring Boot est lancé.");
    }
}

// Gestion de l'affichage dynamique des champs selon le rôle (Ajusté pour correspondre au HTML)
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

// Exportation dans le scope global pour que le HTML puisse les déclencher
globalThis.handleSubscription = handleSubscription;
globalThis.toggleFields = toggleFields;