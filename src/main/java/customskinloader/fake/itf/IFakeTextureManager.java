package customskinloader.fake.itf;

import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public interface IFakeTextureManager {
    interface V1 {
        default boolean func_229263_a_(Identifier textureLocation, Texture textureObj) {
            return ((TextureManager) this).loadTexture(textureLocation, (Texture) textureObj);
        }

        default Texture func_229267_b_(Identifier textureLocation) {
            return (Texture) ((TextureManager) this).getTexture(textureLocation);
        }

        default Texture getTexture(Identifier textureLocation, Texture textureObj) {
            return func_229267_b_(textureLocation);
        }
    }

    interface V2 {
        default void func_229263_a_(Identifier textureLocation, Texture textureObj) {
            ((IFakeTextureManager.V1) this).func_229263_a_(textureLocation, textureObj);
        }
    }
}
