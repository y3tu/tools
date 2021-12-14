import { createStore } from 'vuex'

import settings from './modules/settings'
import tagsView from './modules/tagsView'
import app from './modules/app'

import getters from './getters'


const store = createStore({
    modules: {
        settings,
        app,
        tagsView
    },
    getters
})

export default store
