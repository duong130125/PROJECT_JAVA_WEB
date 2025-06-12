package ra.edu.repository;

import ra.edu.entity.Admin;

public interface AuthAdminRepo {
    Admin login(String username, String password);
    boolean saveRegister(Admin admin);
    boolean existsByUsername(String username);
}
