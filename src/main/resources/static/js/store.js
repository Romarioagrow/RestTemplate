import Vue   from 'vue'
import Vuex  from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)
export default new Vuex.Store({
    state: {
        currentUser: null,
        currentOrder: null
    },
    mutations: {
        setCurrentUser(currentState, user) {
            currentState.currentUser = user
        },
        logoutUser(currentState) {
            currentState.currentUser = null
        },
        setOrderDB(currentState, order) {
            currentState.currentOrder = order
        },
        noCurrentOrder(currentState) {
            currentState.currentOrder = null
        }
    },
    actions: {
        login(context) {
            return axios.get('http://localhost:9000/auth').then((user) => {
                context.commit('setCurrentUser', user.data)
            })
        },
        logout(context) {
            context.commit('logoutUser')
        },
        updateOrder(context, order) {
            context.commit('setOrderDB', order)
        },
        acceptOrder(context) {
            context.commit('noCurrentOrder')
        }
    },
    plugins: [createPersistedState()]
});