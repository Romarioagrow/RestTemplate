import Vue   from 'vue'
import Vuex  from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)
import router from './router'
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
            let sessionProducts
            if (this.state.currentOrder != null) {
                sessionProducts = this.state.currentOrder.orderedProducts
            }
            return axios.post('/auth/addSessionProductToUserOrder', sessionProducts).then((user) => {
                context.commit('setCurrentUser', user.data[0])
                context.commit('setOrderDB', user.data[1])
                router.push('/order')

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
                let productID = key.replace('=','')
                if (!this.state.orderedProducts.includes(productID))
                    context.commit('pushOrderedProduct', key.replace('=',''))
            }
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
        },
        redirectToRoot() {
            router.push('/')
        }

    },
    plugins: [createPersistedState()]
});