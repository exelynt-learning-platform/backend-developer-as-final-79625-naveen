package com.example.booking.service;

import com.example.booking.dto.ResourceRequest;
import com.example.booking.dto.ResourceResponse;
import com.example.booking.entity.BookableResource;
import com.example.booking.exception.ApiExceptions;
import com.example.booking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        BookableResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Resource not found"));
        return mapToResponse(resource);
    }

    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        BookableResource resource = BookableResource.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .active(request.getActive())
                .defaultPrice(request.getDefaultPrice())
                .build();
        return mapToResponse(resourceRepository.save(resource));
    }

    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        BookableResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ApiExceptions.NotFoundException("Resource not found"));
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setActive(request.getActive());
        resource.setDefaultPrice(request.getDefaultPrice());
        return mapToResponse(resourceRepository.save(resource));
    }

    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ApiExceptions.NotFoundException("Resource not found");
        }
        resourceRepository.deleteById(id);
    }

    private ResourceResponse mapToResponse(BookableResource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setName(resource.getName());
        response.setDescription(resource.getDescription());
        response.setType(resource.getType());
        response.setActive(resource.getActive());
        response.setDefaultPrice(resource.getDefaultPrice());
        return response;
    }
}
