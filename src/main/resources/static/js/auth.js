// js/auth.js

// Configuration Keycloak
let keycloakInstance;
if (typeof Keycloak !== 'undefined') {
    keycloakInstance = new Keycloak({
        url: 'http://localhost:7080',
        realm: 'maket-realm',
        clientId: 'maket-frontend'
    });
    globalThis.keycloak = keycloakInstance;
}

// Décodeur de JWT optimisé pour extraire les rôles
function getUserRoles(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replaceAll('-', '+').replaceAll('_', '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.codePointAt(0).toString(16)).slice(-2);
        }).join(''));

        const payload = JSON.parse(jsonPayload);
        
        if (payload?.resource_access?.['maket-frontend']?.roles) {
            return payload.resource_access['maket-frontend'].roles;
        } else if (payload?.realm_access?.roles) {
            return payload.realm_access.roles;
        }
        return [];
    } catch (e) {
        console.error("Erreur lors du décodage du token :", e);
        return [];
    }
}

// Redirection automatique à la connexion selon les privilèges
function redirectByUserRole(token) {
    const roles = getUserRoles(token);
    console.log("Rôles détectés :", roles);
    const origin = globalThis.location.origin;

    if (roles.includes('admin')) {
        globalThis.location.href = origin + '/views/admin-dashboard.html';
    } else if (roles.includes('vendeur')) {
        globalThis.location.href = origin + '/views/gestion-boutique.html';
    } else if (roles.includes('livreur')) {
        globalThis.location.href = origin + '/views/scanner-action.html';
    } else {
        globalThis.location.href = origin + '/views/blog-catalogue.html';
    }
}
globalThis.redirectByUserRole = redirectByUserRole;

// Gardien de sécurité à appeler sur chaque page HTML privée
globalThis.protectPage = (requiredRole) => {
    const token = localStorage.getItem('access_token');
    const origin = globalThis.location.origin;

    if (!token) {
        globalThis.location.href = origin + '/login.html';
        return;
    }

    const roles = getUserRoles(token);
    if (!roles.includes(requiredRole)) {
        alert("Accès interdit ! Rôle '" + requiredRole + "' requis.");
        redirectByUserRole(token);
    }
};

// Initialisation au chargement de la page d'accueil
async function initAuth() {
    const token = localStorage.getItem('access_token');
    if (token) {
        console.log("Utilisateur déjà connecté (Token trouvé)");
        return true;
    }
    
    if (globalThis.keycloak) {
        try {
            return await globalThis.keycloak.init({
                onLoad: 'check-sso',
                checkLoginIframe: false,
                pkceMethod: 'S256'
            });
        } catch (error) {
            console.error("Échec de l'initialisation Auth:", error);
            return false;
        }
    }
    return false;
}
globalThis.initAuth = initAuth;

// --- CONNEXION ---
globalThis.login = async (username, password) => {
    if (!username || !password) {
        alert("Veuillez remplir tous les champs.");
        return;
    }

    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('client_id', 'maket-frontend');
    params.append('username', username);
    params.append('password', password);
    params.append('scope', 'openid profile email'); // AJOUT : Très important pour certaines versions de Keycloak

    try {
        const response = await fetch('http://localhost:7080/realms/maket-realm/protocol/openid-connect/token', {
            method: 'POST',
            body: params,
            headers: { 
                'Content-Type': 'application/x-www-form-urlencoded'
                // Note: On n'envoie PAS d'Authorization header ici car le client est Public
            }
        });

        if (!response.ok) {
            // Pour débugger l'erreur 400, on regarde le contenu de la réponse
            const errorData = await response.json();
            console.error("Détails erreur Keycloak :", errorData);
            throw new Error(errorData.error_description || "Identifiants incorrects.");
        }

        const tokens = await response.json();
        
        localStorage.setItem('access_token', tokens.access_token);
        localStorage.setItem('refresh_token', tokens.refresh_token);

        console.log("Connexion réussie !");
        redirectByUserRole(tokens.access_token);

    } catch (error) {
        console.error("Erreur de connexion :", error);
        alert("Échec : " + error.message);
    }
};

// --- DECONNEXION ---
globalThis.logout = async () => {
    const refreshToken = localStorage.getItem('refresh_token');

    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');

    if (refreshToken) {
        const params = new URLSearchParams();
        params.append('client_id', 'maket-frontend');
        params.append('refresh_token', refreshToken);

        try {
            await fetch('http://localhost:7080/realms/maket-realm/protocol/openid-connect/logout', {
                method: 'POST',
                body: params,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });
        } catch (error) {
            console.error("Erreur déconnexion Keycloak :", error);
        }
    }
    globalThis.location.href = globalThis.location.origin + '/index.html';
};