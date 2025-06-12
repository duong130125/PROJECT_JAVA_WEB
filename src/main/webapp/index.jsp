<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Mobile Shop - Layout</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <style>
        .sidebar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
        }
        .sidebar-brand {
            padding: 1rem;
            color: white;
            font-weight: bold;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        .nav-link {
            color: rgba(255, 255, 255, 0.8);
            padding: 0.75rem 1rem;
            margin: 0.25rem 0;
            border-radius: 8px;
            transition: all 0.3s ease;
        }
        .nav-link:hover, .nav-link.active {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            transform: translateX(5px);
        }
        .nav-link i {
            width: 20px;
            margin-right: 10px;
        }
        .main-content {
            background: #f8fafc;
            min-height: 100vh;
        }
        .sign-out-btn {
            position: absolute;
            bottom: 1rem;
            left: 1rem;
            right: 1rem;
            color: rgba(255, 255, 255, 0.8);
            border: 1px solid rgba(255, 255, 255, 0.3);
            background: transparent;
            padding: 0.5rem;
            border-radius: 8px;
            transition: all 0.3s ease;
        }
        .sign-out-btn:hover {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }
        .content-area {
            background: white;
            border-radius: 12px;
            padding: 2rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
            margin: 2rem;
        }
    </style>
</head>
<body>
<div class="container-fluid p-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 sidebar position-relative">
            <div class="sidebar-brand">
                <i class="fas fa-mobile-alt"></i>
                Mobile <span style="color: #ed8936;">SHOP</span>
            </div>

            <nav class="nav flex-column p-3">
                <a class="nav-link active" href="/" data-page="dashboard.html">
                    <i class="fas fa-tachometer-alt"></i> Dashboard
                </a>
                <a class="nav-link" href="#" data-page="customers.html">
                    <i class="fas fa-users"></i> Customers
                </a>
                <a class="nav-link" href="#" data-page="products.html">
                    <i class="fas fa-box"></i> Products
                </a>
                <a class="nav-link" href="#" data-page="invoices.html">
                    <i class="fas fa-file-invoice"></i> Invoices
                </a>
            </nav>

            <button class="sign-out-btn btn">
                <i class="fas fa-sign-out-alt me-2"></i> Sign out
            </button>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 main-content">
            <div class="content-area" id="main-content-area">
            </div>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script>
    $(document).ready(function () {
        // Tải Dashboard mặc định
        $("#main-content-area").load("views/dashboard.html");

        // Xử lý click menu
        $(".nav-link").click(function (e) {
            e.preventDefault();

            // Active link
            $(".nav-link").removeClass("active");
            $(this).addClass("active");

            // Tải nội dung tương ứng
            const page = $(this).data("page");
            $("#main-content-area").load("pages/" + page);
        });

        // Đăng xuất
        $(".sign-out-btn").click(function () {
            if (confirm("Bạn có chắc chắn muốn đăng xuất?")) {
                alert("Đang đăng xuất...");
                // location.href = 'login.html'; // nếu có trang login
            }
        });
    });
</script>
</body>
</html>
