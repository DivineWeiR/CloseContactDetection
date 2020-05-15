"""CloseContactManager URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from CloseContactManager import view

urlpatterns = [
    path('', view.go_home),
    path('admin/admin_home/', view.go_home),
    path('admin/all_list/', view.show_all_list),
    path('admin/close_contacts_list/', view.show_contacts_list),
    path('admin/patients_list/', view.show_patients_list),
    # path('admin/helps_and_reports_list/', view.show_helps_and_reports_list),
    path('admin/helps_list/', view.show_helps_list),
    path('admin/reports_list/', view.show_reports_list),
    path('admin/check_close_contact/', view.check_close_contact),
    path('admin/check_help_and_report/', view.check_help_and_report),
    path('admin/delete_patient/', view.delete_patient),
    path('admin/show_personal_page/', view.show_personal_page),
    path('admin/show_add_user_page/', view.show_add_user_page),
    path('admin/add_user/', view.add_user),
    path('admin/get_administrative_tree', view.get_administrative_tree),
    path('admin/modify_user_status/', view.modify_user_status),
    path('admin/update_disease_level/', view.update_disease_level),
    path('show_login_page/', view.show_login_page),
    path('show_register_page/', view.show_register_page),
    path('admin_login/', view.admin_login),
    path('admin_register/', view.admin_register),
    path('admin_logout/', view.admin_logout),
    path('download/', view.download, name="download"),
    path('request_patients/', view.client_get_patients),
    path('request_patients_poi/', view.client_get_poi_by_date),
    path('request_patients_poi_new/', view.client_get_poi_by_date_high_level),
    path('request_disease_level/', view.client_get_disease_level),
    path('post_register_data/', view.client_post_register_info),
    path('post_self_data/', view.client_post_user),
    path('post_help/', view.client_post_help),
    path('post_report/', view.client_post_report),
    path('track_search/', view.track_search)
]
