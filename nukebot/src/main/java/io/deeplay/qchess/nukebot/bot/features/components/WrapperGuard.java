package io.deeplay.qchess.nukebot.bot.features.components;

/**
 * Следит за последней оберткой над классом {@link T}, тем самым позволяя писать расширения для
 * {@link T} независимо друг от друга.<br>
 * Для использования необходимо при создании объекта класса {@link T} по объекту расширения E e; (E
 * extends {@link T}), вызвать:<br>
 *
 * <pre>
 *     super(e);
 *     e.{@link #updateLastWrapper}(this);
 * </pre>
 *
 * При обычном создании объекта класса {@link T}, нужно вызвать:
 *
 * <pre>
 *     super(null);
 * </pre>
 */
public abstract class WrapperGuard<T extends WrapperGuard<? super T>> {
    /** Последняя обертка над текущим объектом */
    private T lastWrapper;

    protected WrapperGuard(final T lastWrapper) {
        this.lastWrapper = lastWrapper;
    }

    /**
     * Рекурсивно обновляет последнюю обертку над текущим объектом у этого объекта и у других
     * оберток над этим объектом
     */
    public final void updateLastWrapper(final T newLastWrapper) {
        if (lastWrapper == null) lastWrapper = newLastWrapper;
        else if (lastWrapper != newLastWrapper) {
            final T oldWrapper = lastWrapper;
            lastWrapper = newLastWrapper;
            oldWrapper.updateLastWrapper(newLastWrapper);
        }
    }

    /** @return последняя обертка над текущим объектом */
    protected final T getLastWrapper() {
        return lastWrapper;
    }
}
