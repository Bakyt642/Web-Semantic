from django import forms

class NameForm(forms.Form):
    depar = forms.CharField( max_length=100)


