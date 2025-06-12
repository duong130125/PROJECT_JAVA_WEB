package ra.edu.service;

import ra.edu.entity.Admin;

public interface AuthAdminService {
    Admin login(String username, String password);
    boolean saveRegister(Admin admin);
    boolean existsByUsername(String username);
}
