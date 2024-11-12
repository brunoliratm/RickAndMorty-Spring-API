package com.rickmorty.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.InfoDto;
import com.rickmorty.DTO.LocationDto;
import com.rickmorty.Utils.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;



@Slf4j
@Service
public class LocationService {

    private static final String URL_API = "https://rickandmortyapi.com/api";

    private final ObjectMapper objectMapper;

    @Autowired
    public LocationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ApiResponseDto findAllLocations(Integer page) {
        try{
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API + "/location/" + (page != null ? "?page=" +page : "")))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ApiResponseDto<LocationDto> apiResponseDto = objectMapper.readValue(response.body(),
                    new TypeReference<ApiResponseDto<LocationDto>>() {});
            return RewriteApiResponse(apiResponseDto);
        } catch (Exception e) {
            System.out.println("Um erro aconteceu"+e.getMessage());
        }
        return null;
    }

    public LocationDto getLocationById(String id) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API + "/location/" + id))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), LocationDto.class);
        } catch (Exception e) {
            System.out.println("Um erro ocorreu ao buscar a localização com ID " + id + ": " + e.getMessage());

        }
        return null;
    }

    private ApiResponseDto<LocationDto> RewriteApiResponse(ApiResponseDto<LocationDto> apiResponseDto) {
        InfoDto updatedInfo = RewriteInfoDto(apiResponseDto.info());

        List<LocationDto> updatedResults = new ArrayList<>();
        for (LocationDto location : apiResponseDto.results()) {
            LocationDto updatedLocation = rewriteLocationDto(location);
            updatedResults.add(updatedLocation);
        }
        return new ApiResponseDto<>(updatedInfo, updatedResults);
    }


    private static InfoDto RewriteInfoDto(InfoDto originalInfo) {
        return new InfoDto(
                originalInfo.count(),
                originalInfo.pages(),
                originalInfo.next() != null ? originalInfo.next().replace("https://rickandmortyapi.com/api/location/", Config.base_url + "/api/locations") : null,
                originalInfo.prev() != null ? originalInfo.prev().replace("https://rickandmortyapi.com/api/location/", Config.base_url + "/api/locations") : null
        );
    }

    private LocationDto rewriteLocationDto(LocationDto location) {
        List<String> updatedResidents = new ArrayList<>();
        for (String resident : location.residents()) {
            updatedResidents.add(resident.replace("https://rickandmortyapi.com/api/character/", Config.base_url + "/characteres/"));
        }

        return new LocationDto(
                location.id(),
                location.name(),
                location.type(),
                location.dimension(),
                updatedResidents,
                location.url().replace("https://rickandmortyapi.com/api/location/", Config.base_url + "/locations/")
        );
    }


}