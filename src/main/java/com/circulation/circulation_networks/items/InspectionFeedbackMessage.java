package com.circulation.circulation_networks.items;

import org.jetbrains.annotations.Nullable;

public final class InspectionFeedbackMessage {

    private final String messageKey;
    private final String detailKey;

    private InspectionFeedbackMessage(String messageKey, @Nullable String detailKey) {
        this.messageKey = messageKey;
        this.detailKey = detailKey;
    }

    @Nullable
    public static InspectionFeedbackMessage fromValidation(InspectionConfigurationTarget.ValidationStatus validationStatus) {
        if (!validationStatus.hasMessage()) {
            return null;
        }
        return new InspectionFeedbackMessage(validationStatus.getMessageKey(), null);
    }

    public static InspectionFeedbackMessage fromApplyResult(InspectionConfigurationApplyResult applyResult) {
        return new InspectionFeedbackMessage(applyResult.messageKey(), applyResult.detailKey());
    }

    public String messageKey() {
        return messageKey;
    }

    public String detailKey() {
        return detailKey;
    }

    public boolean hasDetailKey() {
        return detailKey != null;
    }
}