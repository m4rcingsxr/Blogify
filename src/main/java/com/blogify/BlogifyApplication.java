package com.blogify;

import com.blogify.entity.Role;
import com.blogify.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(
		scanBasePackages={"com.blogify"}
)
@RequiredArgsConstructor
public class BlogifyApplication implements CommandLineRunner {

	private final RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(BlogifyApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}


	@Override
	public void run(String... args) throws Exception {
		initializeRoles();
	}

	public void initializeRoles() {
		String[] roles = {"ROLE_ADMIN,Administrator with full access",
						  "ROLE_EDITOR,Editor with access to edit content",
						  "ROLE_USER,Regular user with limited access"};
		List<Role> newRoles = new ArrayList<>();
		for (String role : roles) {
			String[] split = role.split(",");
			String roleName = split[0];
			String roleDescription = split[1];

			if (roleRepository.findByName(roleName).isEmpty()) {
				Role newRole = new Role();
				newRole.setName(roleName);
				newRole.setDescription(roleDescription);
				newRoles.add(newRole);
			}
		}

		if (!newRoles.isEmpty()) {
			roleRepository.saveAll(newRoles);
		}
	}

}
