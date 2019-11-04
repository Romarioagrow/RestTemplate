import Vue   from 'vue'
import Vuex  from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)
export default new Vuex.Store({
    state: {
        currentUser: null,
        currentOrder: null,
        orderedProducts: []
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
        },
        pushOrderedProduct(currentState, productID) {
            currentState.orderedProducts.push(productID)
        },
        removeProductFromOrdered(currentState, productID) {
            currentState.orderedProducts.splice(currentState.orderedProducts.indexOf(productID), 1);
        },
        clearOrderedProducts(currentState) {
            currentState.orderedProducts = []
        }

    },
    actions: {
        login(context) {
            return axios.get('/auth').then((user) => {
                context.commit('setCurrentUser', user.data)

            }).catch(reason => {
                console.log(reason)
            })
        },
        logout(context) {
            context.commit('logoutUser')
        },
        updateOrder(context, order) {
            context.commit('setOrderDB', order)

            for (const [key, value] of Object.entries(order.orderedProducts)) {
                //console.log(key, value)
                context.commit('pushOrderedProduct', key.replace('=',''))
            }

            /*order.orderedProducts.forEach((amount, productID) => {
                context.commit('addOrderedProduct', productID)
            })*/
        },
        acceptOrder(context) {
            context.commit('noCurrentOrder')
        },
        removeOrder(context) {
            context.commit('noCurrentOrder')
        },
        addOrderedProduct(context, productID) {
            context.commit('pushOrderedProduct', productID)
        },
        removeOrderedProduct(context, productID) {
            context.commit('removeProductFromOrdered', productID)
        },
        clearOrderedProducts(context) {
            context.commit('clearOrderedProducts')
        }

    },
    plugins: [createPersistedState()]
});