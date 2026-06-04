package com.maket.maket_backend.modules.identity.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maket.maket_backend.modules.identity.dto.UserRegistrationDTO;
import com.maket.maket_backend.modules.identity.dto.UserResponseDTO;
import com.maket.maket_backend.modules.identity.dto.UserUpdateDTO;
import com.maket.maket_backend.modules.identity.model.User;
import com.maket.maket_backend.modules.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 1. Synchronisation (ton code existant, bien propre)
    @Transactional
    public UserResponseDTO syncUserWithKeycloak(UserRegistrationDTO dto) {
        User user = userRepository.findByKeycloakId(dto.getKeycloakId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setKeycloakId(dto.getKeycloakId());
                    newUser.setEmail(dto.getEmail());
                    newUser.setFirstName(dto.getFirstName());
                    newUser.setLastName(dto.getLastName());
                    // Dans UserService.java, modifie la ligne 28 dans syncUserWithKeycloak :
                    newUser.setRole(com.maket.maket_backend.modules.identity.model.Role.valueOf(dto.getRole().toUpperCase()));
                    return userRepository.save(newUser);
                });
        return mapToResponseDTO(user);
    }

    // 2. Récupération du profil
    public UserResponseDTO getUserProfile(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return mapToResponseDTO(user);
    }

    // 3. Mise à jour du profil (pour le UserUpdateDTO)
    @Transactional
    public UserResponseDTO updateUser(String keycloakId, UserUpdateDTO updateDTO) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        
        return mapToResponseDTO(userRepository.save(user));
    }

    // Petit helper pour transformer l'entité en DTO (le relais propre vers le Front)
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setKeycloakId(user.getKeycloakId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole().name()); // On transforme l'Enum en String
        return response;
    }
}