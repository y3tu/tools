const db = {
    put(key: string, value: string) {
        localStorage.setItem(key, value);
    },
    get(key: string, defaultValue = {}) {
        return JSON.parse(<string>localStorage.getItem(key)) || defaultValue
    },
    remove(key: string) {
        localStorage.removeItem(key);
    },
    clear() {
        localStorage.clear();
    }
}

export default db