package github.rainbowmori.test.block;

import github.rainbowmori.rainbowapi.object.cache.CacheData;
import java.util.UUID;

public class EffectBlocks implements CacheData<UUID> {

  private final UUID uuid;

  public EffectBlocks(UUID uuid) {
    this.uuid = uuid;
  }

  public boolean hasLuckOre;

  @Override
  public UUID getKey() {
    return uuid;
  }
}
