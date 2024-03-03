package fishcute.celestialmain.api.minecraft.wrappers;

public interface IResourceLocationWrapper {
    interface Factory {
        IResourceLocationWrapper build(String name);
    }
}
