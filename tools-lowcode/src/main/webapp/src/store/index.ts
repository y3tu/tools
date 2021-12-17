import {createStore} from 'vuex'

import app from './modules/app'
// @ts-ignore
import design from './modules/design'
import getters from './getters'


const store = createStore({
    modules: {
        app,
        design
    },
    getters
})

export default store
