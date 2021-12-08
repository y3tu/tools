import {createStore} from 'vuex'

const defaultState = {
    count: 0
}

export default createStore({
    state() {
        return defaultState
    },
    mutations: {},
    actions: {},
    getters: {}
})