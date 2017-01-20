package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;

public class AttackDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    private final Conductor conductor;
    private final AttackDetector attackDetector;
    private IAudioRenderTarget targetWhereAttackWasDetected = null;

    public AttackDetectionPipe(Conductor c) {
        conductor = c;
        attackDetector = new AttackDetector(conductor.ATTACK_DIFFERENCE_THRESHOLD, conductor.ATTACK_ENERGY_THRESHOLD);
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {
    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
        try {
            targetWhereAttackWasDetected = attackDetector.analyze(target); // returns null if no attack was detected
            conductor.setAttackDetected(targetWhereAttackWasDetected);
        } catch (Throwable t) {
            throw new RenderCommandException(t);
        }
    }
}
