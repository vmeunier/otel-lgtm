export interface Smartphone {
  id: number;
  brand: string;
  model: string;
  description: string;
  price: number;
  stock: number;
  imageUrl: string;
}

export interface CartItem {
  id?: number;
  smartphoneId: number;
  smartphoneBrand: string;
  smartphoneModel: string;
  smartphonePrice: number;
  quantity: number;
}

export interface Cart {
  id: number;
  userId: string;
  items: CartItem[];
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  id: number;
  smartphoneBrand: string;
  smartphoneModel: string;
  smartphonePrice: number;
  quantity: number;
}

export interface Order {
  id: number;
  userId: string;
  cartId: number;
  totalAmount: number;
  paymentMethod: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED';
  failureReason?: string;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  cartId: number;
  paymentMethod: string;
}

export interface UpdateQuantityRequest {
  quantity: number;
}
