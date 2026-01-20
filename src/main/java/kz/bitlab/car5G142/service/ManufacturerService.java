package kz.bitlab.car5G142.service;

import kz.bitlab.car5G142.entity.Manufacturer;
import kz.bitlab.car5G142.repository.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;

    public List<Manufacturer> getAll() {
        return manufacturerRepository.findAll();
    }
}
