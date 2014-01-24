[@ww.textfield labelKey="obs.url" name="obsUrl" required='true' cssClass="long-field" /]
[@ww.textfield labelKey="obs.username" name="obsUsername" required='true'/]

[#if context.get("obsPassword")?has_content]
    [@ww.checkbox labelKey='obs.changePassword' toggle=true name='passwordChange' /]
    [@ui.bambooSection dependsOn='passwordChange' showOn=true]
        [@ww.password labelKey='obs.password' name='obsPassword' required='true' /]
    [/@ui.bambooSection]
[#else]
    [@ww.hidden name='passwordChange' value=true /]
    [@ww.password labelKey='obs.password' name='obsPassword' required='true' /]
[/#if]
