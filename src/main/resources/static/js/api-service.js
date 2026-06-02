const API_BASE_URL = "http://localhost:8099"; 

const ApiService = {
    async fetch(endpoint, options = {}) {
        const token = localStorage.getItem('access_token');
        
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token && token !== "null" && token !== "undefined") {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, { ...options, headers });
            
            if (response.status === 401) {
                localStorage.removeItem('access_token');
                localStorage.removeItem('refresh_token');
                console.warn("Session expirée.");
                return null;
            }

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `Erreur API: ${response.status}`);
            }

            // --- CORRECTION SonarLint (S6582) ---
            // On utilise ?. pour vérifier le type de contenu de manière concise
            const contentType = response.headers.get("content-type");
            if (response.status === 204 || !contentType?.includes("application/json")) {
                return null;
            }

            return await response.json();
        } catch (error) {
            console.error("Erreur réseau ou API :", error);
            throw error;
        }
    },

    async post(endpoint, data) {
        return this.fetch(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    async delete(endpoint) {
        return this.fetch(endpoint, { method: 'DELETE' });
    }
};

globalThis.ApiService = ApiService;