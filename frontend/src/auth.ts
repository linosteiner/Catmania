export function setToken(token: string | null) {
    if (token) localStorage.setItem('token', token);
    else localStorage.removeItem('token');
    window.dispatchEvent(new Event('auth-changed'));
}

export function getToken(): string | null {
    return localStorage.getItem('token');
}

export function authHeader(): Record<string, string> {
    const t = getToken();
    return t ? {Authorization: `Bearer ${t}`} : {};
}

export async function login(username: string, password: string) {
    const res = await fetch('/login', {
        method: 'POST',
        headers: {'Content-Type': 'application/json', Accept: 'application/json'},
        body: JSON.stringify({username, password})
    });
    if (!res.ok) {
        const msg = await res.text().catch(() => `${res.status} ${res.statusText}`);
        throw new Error(msg);
    }
    const json = await res.json();
    if (!json.access_token) throw new Error('No token returned');
    setToken(json.access_token);
}

export function logout() {
    setToken(null);
}
