package ra.edu.repository;

import org.springframework.stereotype.Repository;
import ra.edu.entity.Admin;

@Repository
public class AuthAdminRepoImpl implements AuthAdminRepo {

    @Override
    public Admin login(String username, String password) {
        return null;
    }

    @Override
    public boolean saveRegister(Admin admin) {
        return false;
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }
}
