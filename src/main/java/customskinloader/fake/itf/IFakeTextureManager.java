package customskinloader.fake.itf;

import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public interface IFakeTextureManager {
    interface V1 {
        default boolean loadTexture(Identifier textureLocation, Texture textureObj) {
            return ((TextureManager) this).loadTexture(textureLocation, (Texture) textureObj);
        }

        default Texture getTexture(Identifier textureLocation) {
            return (Texture) ((TextureManager) this).getTexture(textureLocation);
        }

        default Texture getTexture(Identifier textureLocation, Texture textureObj) {
            return getTexture(textureLocation);
        }
    }

    interface V2 {
        default void loadTexture(Identifier textureLocation, Texture textureObj) {
            ((IFakeTextureManager.V1) this).loadTexture(textureLocation, textureObj);
        }
    }
}
