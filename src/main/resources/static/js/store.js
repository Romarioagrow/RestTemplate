import Vue  from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)
export default new Vuex.Store({
    state: {
        currentUser: null
    },
    mutations: {
        setCurrentUser(currentState, user) {
            currentState.currentUser = user;
        },
        logoutUser(currentState) {
            currentState.currentUser = null;
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
        }
    },
    plugins: [createPersistedState()]
});