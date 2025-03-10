package com.rickmorty.interfaces;

import com.rickmorty.DTO.FavoriteDto;
import com.rickmorty.DTO.FavoriteResponseDto;
import com.rickmorty.enums.SortFavorite;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

public interface FavoriteServiceInterface {
  void create(FavoriteDto favoriteDto, BindingResult result);

  Page<FavoriteResponseDto> getAllFavorites(Long userId, int page, SortFavorite sort);

  void removeFavorite(Long userId, Long favoriteId);

  void removeAllFavoritesByUserId(Long userId);

}
