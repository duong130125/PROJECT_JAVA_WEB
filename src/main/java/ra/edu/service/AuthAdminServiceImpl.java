package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Admin;
import ra.edu.repository.AuthAdminRepo;

@Service
public class AuthAdminServiceImpl implements AuthAdminService {

    @Autowired
    private AuthAdminRepo authAdminRepo;

    @Override
    public Admin login(String username, String password) {
        return authAdminRepo.login(username, password);
    }

    @Override
    public boolean saveRegister(Admin admin) {
        return authAdminRepo.saveRegister(admin);
    }

    @Override
    public boolean existsByUsername(String username) {
        return authAdminRepo.existsByUsername(username);
    }
}
