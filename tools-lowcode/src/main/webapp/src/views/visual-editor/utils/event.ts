type SimplyListener = (param: any) => void;

export function createEvent() {
    let listeners: SimplyListener[] = [];
    return {
        on: (cb: SimplyListener) => {
            listeners.push(cb);
        },
        off: (cb: SimplyListener) => {
            const index = listeners.indexOf(cb);
            if (index > -1) listeners.splice(index, 1);
        },
        emit: (param?: any) => {
            listeners.forEach(item => item(param));
        }
    }
}