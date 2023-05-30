package github.rainbowmori.test;

import github.rainbowmori.rainbowapi.object.cache.CacheData;
import java.util.UUID;

public class EffectBlock implements CacheData<UUID> {

  private final UUID uuid;

  public EffectBlock(UUID uuid) {
    this.uuid = uuid;
  }

  public boolean hasLuckOre;

  @Override
  public UUID getKey() {
    return uuid;
  }
}
