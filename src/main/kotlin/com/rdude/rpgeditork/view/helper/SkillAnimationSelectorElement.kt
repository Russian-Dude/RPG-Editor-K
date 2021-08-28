package com.rdude.rpgeditork.view.helper

import com.rdude.rpgeditork.wrapper.ParticleResourceWrapper
import ru.rdude.fxlib.containers.selector.SelectorElementWindowProperties

class SkillAnimationSelectorElement(properties: SkillAnimationSelectorElementProperties) : SelectorElementWindowProperties<ParticleResourceWrapper, SkillAnimationSelectorElementProperties>(properties), ParticleHolder {

    override var particle: ParticleResourceWrapper?
        get() = value
        set(value) { this.value = value }
}