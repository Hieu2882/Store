from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from datetime import datetime
import os
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__)
CORS(app)

# Cấu hình SQLite database
basedir = os.path.abspath(os.path.dirname(__file__))
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, 'shopping.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

# Models
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password = db.Column(db.String(120), nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    full_name = db.Column(db.String(100))
    address = db.Column(db.String(200))
    phone = db.Column(db.String(20))
    orders = db.relationship('Order', backref='user', lazy=True)
    cart_items = db.relationship('CartItem', backref='user', lazy=True)

    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'email': self.email,
            'fullName': self.full_name,
            'address': self.address,
            'phone': self.phone
        }

class Product(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(500))
    price = db.Column(db.Float, nullable=False)
    image_url = db.Column(db.String(200))
    stock_quantity = db.Column(db.Integer, default=0)
    order_items = db.relationship('OrderItem', backref='product', lazy=True)
    cart_items = db.relationship('CartItem', backref='product', lazy=True)

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description,
            'price': self.price,
            'imageUrl': self.image_url,
            'stockQuantity': self.stock_quantity
        }

class Order(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    order_date = db.Column(db.DateTime, default=datetime.utcnow)
    total_amount = db.Column(db.Float, nullable=False)
    status = db.Column(db.String(20), default='PENDING')
    shipping_address = db.Column(db.String(200))
    payment_method = db.Column(db.String(50))
    items = db.relationship('OrderItem', backref='order', lazy=True)

    def to_dict(self):
        return {
            'id': self.id,
            'userId': self.user_id,
            'orderDate': self.order_date.isoformat(),
            'totalAmount': self.total_amount,
            'status': self.status,
            'shippingAddress': self.shipping_address,
            'paymentMethod': self.payment_method
        }

class OrderItem(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    order_id = db.Column(db.Integer, db.ForeignKey('order.id'), nullable=False)
    product_id = db.Column(db.Integer, db.ForeignKey('product.id'), nullable=False)
    quantity = db.Column(db.Integer, nullable=False)
    price = db.Column(db.Float, nullable=False)

    def to_dict(self):
        return {
            'id': self.id,
            'orderId': self.order_id,
            'productId': self.product_id,
            'quantity': self.quantity,
            'price': self.price
        }

class CartItem(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    product_id = db.Column(db.Integer, db.ForeignKey('product.id'), nullable=False)
    quantity = db.Column(db.Integer, default=1)

    def to_dict(self):
        return {
            'id': self.id,
            'userId': self.user_id,
            'productId': self.product_id,
            'quantity': self.quantity
        }

# Tạo database và bảng
with app.app_context():
    db.create_all()
    
    # Thêm sản phẩm mẫu nếu chưa có
    if Product.query.count() == 0:
        sample_products = [
            Product(
                name="iPhone 13 Pro",
                description="Apple iPhone 13 Pro 256GB, Graphite",
                price=999.99,
                image_url="https://example.com/iphone13pro.jpg",
                stock_quantity=10
            ),
            Product(
                name="Samsung Galaxy S21",
                description="Samsung Galaxy S21 5G 128GB, Phantom Gray",
                price=799.99,
                image_url="https://example.com/galaxys21.jpg",
                stock_quantity=15
            ),
            Product(
                name="MacBook Pro",
                description="Apple MacBook Pro 14-inch, M1 Pro chip",
                price=1999.99,
                image_url="https://example.com/macbookpro.jpg",
                stock_quantity=8
            ),
            Product(
                name="Sony WH-1000XM4",
                description="Sony WH-1000XM4 Wireless Noise Cancelling Headphones",
                price=349.99,
                image_url="https://example.com/sonyheadphones.jpg",
                stock_quantity=20
            ),
            Product(
                name="Nintendo Switch",
                description="Nintendo Switch with Neon Blue and Neon Red Joy‑Con",
                price=299.99,
                image_url="https://example.com/nintendoswitch.jpg",
                stock_quantity=12
            ),
            Product(
                name="iPad Air",
                description="Apple iPad Air (4th generation) 64GB, Space Gray",
                price=599.99,
                image_url="https://example.com/ipadair.jpg",
                stock_quantity=10
            ),
            Product(
                name="Samsung 4K TV",
                description="Samsung 55-inch Class QLED 4K Smart TV",
                price=899.99,
                image_url="https://example.com/samsungtv.jpg",
                stock_quantity=5
            ),
            Product(
                name="AirPods Pro",
                description="Apple AirPods Pro with Active Noise Cancellation",
                price=249.99,
                image_url="https://example.com/airpodspro.jpg",
                stock_quantity=25
            )
        ]
        
        for product in sample_products:
            db.session.add(product)
        
        db.session.commit()

# Routes
@app.route('/')
def home():
    return "Shopping API is running!"

# User endpoints
@app.route('/api/users/register', methods=['POST'])
def register():
    data = request.get_json()
    
    if User.query.filter_by(username=data['username']).first():
        return jsonify({'error': 'Username already exists'}), 400
    
    if User.query.filter_by(email=data['email']).first():
        return jsonify({'error': 'Email already exists'}), 400
    
    user = User(
        username=data['username'],
        password=generate_password_hash(data['password']),
        email=data['email'],
        full_name=data['fullName'],
        address=data['address'],
        phone=data['phone']
    )
    
    db.session.add(user)
    db.session.commit()
    
    return jsonify(user.to_dict()), 201

@app.route('/api/users/login', methods=['POST'])
def login():
    data = request.get_json()
    user = User.query.filter_by(username=data['username']).first()
    
    if user and check_password_hash(user.password, data['password']):
        return jsonify(user.to_dict())
    
    return jsonify({'error': 'Invalid credentials'}), 401

# Product endpoints
@app.route('/api/products', methods=['GET'])
def get_products():
    products = Product.query.all()
    return jsonify([product.to_dict() for product in products])

@app.route('/api/products/search', methods=['GET'])
def search_products():
    query = request.args.get('query', '').lower()
    if not query:
        return jsonify([])
    
    products = Product.query.filter(Product.name.ilike(f'%{query}%')).all()
    return jsonify([product.to_dict() for product in products])

@app.route('/api/products/<int:product_id>', methods=['GET'])
def get_product(product_id):
    product = Product.query.get_or_404(product_id)
    return jsonify(product.to_dict())

# Cart endpoints
@app.route('/api/cart/<int:user_id>', methods=['GET'])
def get_cart(user_id):
    cart_items = CartItem.query.filter_by(user_id=user_id).all()
    return jsonify([item.to_dict() for item in cart_items])

@app.route('/api/cart', methods=['POST'])
def add_to_cart():
    data = request.get_json()
    cart_item = CartItem(
        user_id=data['userId'],
        product_id=data['productId'],
        quantity=data['quantity']
    )
    db.session.add(cart_item)
    db.session.commit()
    return jsonify(cart_item.to_dict()), 201

@app.route('/api/cart/<int:cart_item_id>', methods=['PUT'])
def update_cart_item(cart_item_id):
    cart_item = CartItem.query.get_or_404(cart_item_id)
    data = request.get_json()
    cart_item.quantity = data['quantity']
    db.session.commit()
    return jsonify(cart_item.to_dict())

@app.route('/api/cart/<int:cart_item_id>', methods=['DELETE'])
def remove_from_cart(cart_item_id):
    cart_item = CartItem.query.get_or_404(cart_item_id)
    db.session.delete(cart_item)
    db.session.commit()
    return '', 204

# Order endpoints
@app.route('/api/orders/user/<int:user_id>', methods=['GET'])
def get_user_orders(user_id):
    orders = Order.query.filter_by(user_id=user_id).all()
    return jsonify([order.to_dict() for order in orders])

@app.route('/api/orders', methods=['POST'])
def create_order():
    data = request.get_json()
    order = Order(
        user_id=data['userId'],
        total_amount=data['totalAmount'],
        shipping_address=data['shippingAddress'],
        payment_method=data['paymentMethod']
    )
    db.session.add(order)
    db.session.commit()
    
    # Create order items
    for item in data['items']:
        order_item = OrderItem(
            order_id=order.id,
            product_id=item['productId'],
            quantity=item['quantity'],
            price=item['price']
        )
        db.session.add(order_item)
    
    # Clear user's cart
    CartItem.query.filter_by(user_id=data['userId']).delete()
    
    db.session.commit()
    return jsonify(order.to_dict()), 201

if __name__ == '__main__':
    app.run(debug=True) 