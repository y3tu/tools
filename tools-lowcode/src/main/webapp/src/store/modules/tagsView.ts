const state = {
    //访问过的页面
    visitedViews: [],
    //缓存的页面
    cachedViews: []
}

const mutations = {
    //添加访问页面
    ADD_VISITED_VIEW: (state, view) => {
        if (state.visitedViews.some(v => v.path === view.path)) return
        state.visitedViews.push(
            Object.assign({}, view, {
                title: view.meta.title || 'no-name'
            })
        )
    },

    //添加缓存页面
    ADD_CACHED_VIEW: (state, view) => {
        if (state.cachedViews.includes(view.name)) return
        if (!view.meta.noCache) {
            state.cachedViews.push(view.name)
        }
    },

    //关闭访问页面
    DEL_VISITED_VIEW: (state, view) => {
        for (const [i, v] of state.visitedViews.entries()) {
            if (v.path === view.path) {
                state.visitedViews.splice(i, 1)
                break
            }
        }
    },

    //关闭缓存页面
    DEL_CACHED_VIEW: (state, view) => {
        const index = state.cachedViews.indexOf(view.name)
        index > -1 && state.cachedViews.splice(index, 1)
    },

    //关闭其他访问页面
    DEL_OTHERS_VISITED_VIEWS: (state, view) => {
        state.visitedViews = state.visitedViews.filter(v => {
            return v.meta.affix || v.path === view.path
        })
    },

    //关闭其他缓存页面
    DEL_OTHERS_CACHED_VIEWS: (state, view) => {
        const index = state.cachedViews.indexOf(view.name)
        if (index > -1) {
            state.cachedViews = state.cachedViews.slice(index, index + 1)
        } else {
            state.cachedViews = []
        }
    },

    //关闭所有访问页面
    DEL_ALL_VISITED_VIEWS: state => {
        // keep affix tags
        const affixTags = state.visitedViews.filter(tag => tag.meta.affix)
        state.visitedViews = affixTags
    },

    //关闭所有缓存页面
    DEL_ALL_CACHED_VIEWS: state => {
        state.cachedViews = []
    },

    //更新访问页面
    UPDATE_VISITED_VIEW: (state, view) => {
        for (let v of state.visitedViews) {
            if (v.path === view.path) {
                v = Object.assign(v, view)
                break
            }
        }
    },

    //关闭右侧页面
    DEL_RIGHT_VIEWS: (state, view) => {
        const index = state.visitedViews.findIndex(v => v.path === view.path)
        if (index === -1) {
            return
        }
        state.visitedViews = state.visitedViews.filter((item, idx) => {
            if (idx <= index || (item.meta && item.meta.affix)) {
                return true
            }
            const i = state.cachedViews.indexOf(item.name)
            if (i > -1) {
                state.cachedViews.splice(i, 1)
            }
            return false
        })
    },

    //关闭左侧页面
    DEL_LEFT_VIEWS: (state, view) => {
        const index = state.visitedViews.findIndex(v => v.path === view.path)
        if (index === -1) {
            return
        }
        state.visitedViews = state.visitedViews.filter((item, idx) => {
            if (idx >= index || (item.meta && item.meta.affix)) {
                return true
            }
            const i = state.cachedViews.indexOf(item.name)
            if (i > -1) {
                state.cachedViews.splice(i, 1)
            }
            return false
        })
    }

}

const actions = {
    //打开页面
    addView({ dispatch }, view) {
        dispatch('addVisitedView', view)
        dispatch('addCachedView', view)
    },
    //打开已访问页面
    addVisitedView({ commit }, view) {
        commit('ADD_VISITED_VIEW', view)
    },
    //打开缓存页面
    addCachedView({ commit }, view) {
        commit('ADD_CACHED_VIEW', view)
    },

    //关闭页面
    delView({ dispatch, state }, view) {
        return new Promise(resolve => {
            dispatch('delVisitedView', view)
            dispatch('delCachedView', view)
            resolve({
                visitedViews: [...state.visitedViews],
                cachedViews: [...state.cachedViews]
            })
        })
    },

    //关闭访问页面
    delVisitedView({ commit, state }, view) {
        return new Promise(resolve => {
            commit('DEL_VISITED_VIEW', view)
            resolve([...state.visitedViews])
        })
    },
    //关闭缓存页面
    delCachedView({ commit, state }, view) {
        return new Promise(resolve => {
            commit('DEL_CACHED_VIEW', view)
            resolve([...state.cachedViews])
        })
    },

    //关闭其他页面
    delOthersViews({ dispatch, state }, view) {
        return new Promise(resolve => {
            dispatch('delOthersVisitedViews', view)
            dispatch('delOthersCachedViews', view)
            resolve({
                visitedViews: [...state.visitedViews],
                cachedViews: [...state.cachedViews]
            })
        })
    },

    //关闭其他访问页面
    delOthersVisitedViews({ commit, state }, view) {
        return new Promise(resolve => {
            commit('DEL_OTHERS_VISITED_VIEWS', view)
            resolve([...state.visitedViews])
        })
    },

    //关闭其他缓存页面
    delOthersCachedViews({ commit, state }, view) {
        return new Promise(resolve => {
            commit('DEL_OTHERS_CACHED_VIEWS', view)
            resolve([...state.cachedViews])
        })
    },

    //关闭所有页面
    delAllViews({ dispatch, state }, view) {
        return new Promise(resolve => {
            dispatch('delAllVisitedViews', view)
            dispatch('delAllCachedViews', view)
            resolve({
                visitedViews: [...state.visitedViews],
                cachedViews: [...state.cachedViews]
            })
        })
    },

    delAllVisitedViews({ commit, state }) {
        return new Promise(resolve => {
            commit('DEL_ALL_VISITED_VIEWS')
            resolve([...state.visitedViews])
        })
    },
    delAllCachedViews({ commit, state }) {
        return new Promise(resolve => {
            commit('DEL_ALL_CACHED_VIEWS')
            resolve([...state.cachedViews])
        })
    },

    //更新页面
    updateVisitedView({ commit }, view) {
        commit('UPDATE_VISITED_VIEW', view)
    },

    //关闭右侧页面
    delRightTags({ commit }, view) {
        return new Promise(resolve => {
            commit('DEL_RIGHT_VIEWS', view)
            resolve([...state.visitedViews])
        })
    },

    //关闭左侧页面
    delLeftTags({ commit }, view) {
        return new Promise(resolve => {
            commit('DEL_LEFT_VIEWS', view)
            resolve([...state.visitedViews])
        })
    },
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
