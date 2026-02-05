import axios, { AxiosResponse } from 'axios';
import type { Smartphone, Cart, CartItem, Order, CreateOrderRequest, UpdateQuantityRequest } from '../types';

const INVENTORY_SERVICE_URL = 'http://localhost:8083/api';
const CART_SERVICE_URL = 'http://localhost:8085/api';
const ORDER_SERVICE_URL = 'http://localhost:8084/api';

const api = {
  // Inventory Service
  getSmartphones: (): Promise<AxiosResponse<Smartphone[]>> =>
    axios.get(`${INVENTORY_SERVICE_URL}/smartphones`),

  getSmartphone: (id: number): Promise<AxiosResponse<Smartphone>> =>
    axios.get(`${INVENTORY_SERVICE_URL}/smartphones/${id}`),

  // Cart Service
  getCart: (userId: string): Promise<AxiosResponse<Cart>> =>
    axios.get(`${CART_SERVICE_URL}/carts/users/${userId}`),

  addToCart: (userId: string, item: CartItem): Promise<AxiosResponse<Cart>> =>
    axios.post(`${CART_SERVICE_URL}/carts/users/${userId}/items`, item),

  updateCartItem: (cartId: number, itemId: number, quantity: number): Promise<AxiosResponse<Cart>> =>
    axios.put<Cart, AxiosResponse<Cart>, UpdateQuantityRequest>(
      `${CART_SERVICE_URL}/carts/${cartId}/items/${itemId}`,
      { quantity }
    ),

  removeFromCart: (cartId: number, itemId: number): Promise<AxiosResponse<void>> =>
    axios.delete(`${CART_SERVICE_URL}/carts/${cartId}/items/${itemId}`),

  clearCart: (cartId: number): Promise<AxiosResponse<void>> =>
    axios.delete(`${CART_SERVICE_URL}/carts/${cartId}`),

  // Order Service
  createOrder: (cartId: number, paymentMethod: string): Promise<AxiosResponse<Order>> =>
    axios.post<Order, AxiosResponse<Order>, CreateOrderRequest>(
      `${ORDER_SERVICE_URL}/orders`,
      { cartId, paymentMethod }
    ),

  getOrder: (orderId: number): Promise<AxiosResponse<Order>> =>
    axios.get(`${ORDER_SERVICE_URL}/orders/${orderId}`),
};

export default api;
