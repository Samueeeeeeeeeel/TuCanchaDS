package com.example.Reservas.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente para comunicarse con el microservicio de Disponibilidad
 * Crea disponibilidades cuando se crean reservas
 */
@Component
public class DisponibilidadClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${disponibilidad.service.url:http://localhost:8082}")
    private String disponibilidadServiceUrl;
    
    public DisponibilidadClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Crear disponibilidad marcada como no disponible (ocupada por una reserva)
     */
    public void crearDisponibilidadOcupada(Long canchaId, String fechaInicio, String fechaFin) {
        try {
            String url = disponibilidadServiceUrl + "/api/disponibilidades";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> disponibilidad = new HashMap<>();
            disponibilidad.put("canchaId", canchaId);
            disponibilidad.put("fechaInicio", fechaInicio);
            disponibilidad.put("fechaFin", fechaFin);
            disponibilidad.put("disponible", false);
            disponibilidad.put("motivoNoDisponible", "Reservada");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(disponibilidad, headers);
            
            restTemplate.postForEntity(url, request, Object.class);
        } catch (Exception e) {
            // Si falla crear disponibilidad, no es crítico
            // La reserva ya está creada
            System.err.println("Error al crear disponibilidad: " + e.getMessage());
        }
    }
    
    /**
     * Eliminar disponibilidad (liberar horario cuando se cancela una reserva)
     */
    public void eliminarDisponibilidad(Long canchaId, String fechaInicio, String fechaFin) {
        try {
            // Obtener todas las disponibilidades de la cancha
            String urlGet = disponibilidadServiceUrl + "/api/disponibilidades/cancha/" + canchaId;
            ResponseEntity<List> response = restTemplate.getForEntity(urlGet, List.class);
            
            if (response.getBody() != null) {
                // Buscar la disponibilidad que coincide con la fecha y hora
                for (Object item : response.getBody()) {
                    try {
                        JsonNode disponibilidad = objectMapper.valueToTree(item);
                        if (disponibilidad.get("disponible").asBoolean() == false &&
                            disponibilidad.get("fechaInicio").asText().equals(fechaInicio) &&
                            disponibilidad.get("fechaFin").asText().equals(fechaFin)) {
                            
                            // Eliminar esta disponibilidad
                            Long disponibilidadId = disponibilidad.get("id").asLong();
                            String urlDelete = disponibilidadServiceUrl + "/api/disponibilidades/" + disponibilidadId;
                            restTemplate.exchange(urlDelete, HttpMethod.DELETE, null, Void.class);
                            break;
                        }
                    } catch (Exception e) {
                        // Continuar con la siguiente
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar disponibilidad: " + e.getMessage());
        }
    }
}
