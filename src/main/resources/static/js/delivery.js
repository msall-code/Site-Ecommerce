// js/delivery.js

async function confirmPickup(productId) {
    if (!confirm("Confirmer la récupération du colis chez le vendeur ?")) return;

    try {
        await globalThis.ApiService.fetch(`/delivery/pickup/${productId}`, { method: 'POST' });
        alert("Colis récupéré ! Statut mis à jour.");
        location.reload(); 
    } catch (error) {
        alert("Erreur lors de la récupération : " + error.message);
    }
}

async function finalizeDelivery(productId) {
    if (!confirm("Le client a-t-il bien reçu son colis ?")) return;

    try {
        await globalThis.ApiService.fetch(`/delivery/complete/${productId}`, { method: 'POST' });
        alert("Livraison terminée ! Merci.");
        location.reload();
    } catch (error) {
        alert("Erreur : " + error.message);
    }
}

function onScanSuccess(decodedText) {
    console.log("Code scanné : " + decodedText);
    confirmPickup(decodedText);
}

globalThis.confirmPickup = confirmPickup;
globalThis.finalizeDelivery = finalizeDelivery;
globalThis.onScanSuccess = onScanSuccess;