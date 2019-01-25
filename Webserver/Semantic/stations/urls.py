
from django.conf.urls import url
from django.urls import path

from . import views

urlpatterns = [
    path('', views.show, name='home'),
    path('query2', views.Query, name='query2'),
    path('query3', views.Query, name='query3'),



    # url(r'^station/',views.show),
    url(r'^search/$',views.search_engine),
    url(r'^search/input$',views.queryA),
    url(r'^test-view/$',views.test_view),
    url(r'^search/query2$',views.queryB),
    url(r'^search/query3$',views.queryC)


]
