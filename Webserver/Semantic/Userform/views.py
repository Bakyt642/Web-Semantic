from django.shortcuts import render
from django.http import HttpResponse
from django.views.generic import TemplateView
from .forms import NameForm
news=[
    {
        'title':'Our first record',
        'text':'just',
        'date': '1 january',
        'author':'Bakyt'
    }
]
#
def home(request):

    return render(request, 'Userform/home.html')


# def contacts(request):
#     return render(request, 'blog/contacts.html',{'title':'About us'})
# # Create your views here.



# def get_name(request):
#     # if this is a POST request we need to process the form data
#     if request.method == 'POST':
#         # create a form instance and populate it with data from the request:
#         form = NameForm(request.POST)
#         # check whether it's valid:
#         if form.is_valid():
#             # process the data in form.cleaned_data as required
#             # ...
#             # redirect to a new URL:
#             return HttpResponseRedirect('/thanks/')
#
#     # if a GET (or any other method) we'll create a blank form
#     else:
#         form = NameForm()
#
#     return render(request, 'name.html', {'form': form})





class HomeView(TemplateView):
    template_name = 'Userform/home.html'
    def get(self,request):
        form=NameForm()
        return render(request,self.template_name,{'form':form})
