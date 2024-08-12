import pymysql
import random
import string
from datetime import datetime

connection = pymysql.connect(
    host='localhost',
    user='root',
    password='1234',
    database='stylish'
)

def generate_order_number():
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=10))

users_data = {
    1: {"name": "Bob", "phone": "0934567890", "email": "bob.smith@example.com", "address": "中山國中"},
    2: {"name": "John", "phone": "0912345678", "email": "john.doe@example.com", "address": "台北車站"},
    3: {"name": "Mary", "phone": "0923456789", "email": "mary.jane@example.com", "address": "信義安和"},
    4: {"name": "Luke", "phone": "0987654321", "email": "luke@gmail.com", "address": "市政府站"},
    5: {"name": "Anna", "phone": "0945678901", "email": "anna.lee@example.com", "address": "大安森林公園"}
}

try:
    with connection.cursor() as cursor:
        number_of_orders = random.randint(1000, 1001)

        sql = """
        INSERT INTO orders (user_id, shipping, payment, subtotal, freight, total, recipient_name, recipient_phone, recipient_email, recipient_address, recipient_time, status, order_number) 
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """

        for _ in range(number_of_orders):
            user_id = random.randint(1, 5)
            total = random.randint(100, 1000)
            order_number = generate_order_number()
            
            user_info = users_data[user_id]
            shipping = 'delivery'
            payment = 'credit_card'
            subtotal = total * 0.8
            freight = total * 0.2
            recipient_name = user_info["name"]
            recipient_phone = user_info["phone"]
            recipient_email = user_info["email"]
            recipient_address = user_info["address"]
            recipient_time = 'morning'
            status = random.choice(['unpaid', 'paid'])
            created_at = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            
            cursor.execute(sql, (
                user_id, shipping, payment, subtotal, freight, total, 
                recipient_name, recipient_phone, recipient_email, recipient_address, 
                recipient_time, status, order_number
            ))
        
        connection.commit()

finally:
    connection.close()

print(f"成功插入 {number_of_orders} 筆隨機訂單數據。")