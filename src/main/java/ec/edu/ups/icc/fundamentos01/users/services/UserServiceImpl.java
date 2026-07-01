package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    
    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(UserMapper::toModelFromEntity)
                .map(UserMapper::toResponse)
                .toList();
    }

    
    @Override
    public UserResponseDto findOne(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        UserModel model = UserMapper.toModelFromEntity(entity);
        return UserMapper.toResponse(model);
    }

   
    @Override
    public UserResponseDto create(CreateUserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email already registered");
        }

        UserModel model = UserMapper.toModelFromDTO(dto);
        UserEntity entity = UserMapper.toEntityFromModel(model);
        UserEntity savedEntity = userRepository.save(entity);
        UserModel savedModel = UserMapper.toModelFromEntity(savedEntity);
        return UserMapper.toResponse(savedModel);
    }

   
    @Override
    public UserResponseDto update(Long id, UpdateUserDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        UserEntity savedEntity = userRepository.save(entity);
        UserModel model = UserMapper.toModelFromEntity(savedEntity);
        return UserMapper.toResponse(model);
    }

   
    @Override
    public UserResponseDto partialUpdate(Long id, PartialUpdateUserDto dto) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        /* 
            si en el pach llega una nueva contra la ciframos y la guardamos en el usuarilo.
        */
        if (dto.getPassword() != null) {
            entity.setPasswordHash(UserMapper.hash(dto.getPassword()));
        }

        UserEntity savedEntity = userRepository.save(entity);
        UserModel model = UserMapper.toModelFromEntity(savedEntity);
        return UserMapper.toResponse(model);
    }

    
    @Override
    public void delete(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        entity.setDeleted(true);
        userRepository.save(entity);
    }
}
