import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

import axios from 'axios'
export default new Vuex.Store({
    state: {
        currentUser: null
    },

    mutations: {
        setCurrentUser(currentState, user) {
            currentState.currentUser = user;
        }
    },

    actions: {
        login(context) {
            return axios.get('http://localhost:9000/auth').then((user) => {
                context.commit('setCurrentUser', user.data)
                console.log(this.state.currentUser)
            })
        }
    },

    getters: {
    },
});