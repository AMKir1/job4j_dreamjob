<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="ru.job4j.dream.model.Candidate" %>
<%@ page import="ru.job4j.dream.PsqlStore" %>
<%@ page import="ru.job4j.dream.model.City" %>
<%@ page import="java.util.Collection" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
            integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
            crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <title>Работа мечты</title>

</head>
<body>
<%
    String id = request.getParameter("id");
    Candidate candidate = new Candidate(0, "");
    if (id != null) {
        candidate = PsqlStore.instOf().findCandidateById(Integer.parseInt(id));
    }
%>

<div class="container pt-3">
    <div class="row">
        <ul class="nav">
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/">Главная</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/posts.do">Вакансии</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/candidates.do">Кандидаты</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/post/edit.jsp">Добавить вакансию</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%=request.getContextPath()%>/candidate/edit.jsp">Добавить кандидата</a>
            </li>
            <c:if test="${not empty user.name}">
                <li class="nav-item">
                    <a class="nav-link" href="<%=request.getContextPath()%>/login.jsp"> <c:out value="${user.name}"/> |
                        Выйти</a>
                </li>
            </c:if>
            <c:if test="${empty user.name}">
                <li class="nav-item">
                    <a class="nav-link" href="<%=request.getContextPath()%>/login.jsp">Войти</a>
                </li>
            </c:if>
        </ul>
    </div>

    <div class="row">
        <div class="card" style="width: 100%">
            <div class="card-header">
                <% if (id == null) { %>
                Новый кандидат.
                <% } else { %>
                Редактирование кандидата.
                <% } %>
            </div>

            <div class="card-body">
                <form>
                    <div class="form-group">
                        <label>Имя</label>
                        <input type="text" class="form-control form-select-lg mb-3" aria-label=".form-select-lg example" id="name" name="name" value="<%=candidate.getName()%>">
                        <label>Город</label>
                        <select class="form-select" id="city" aria-label="Default select example">

                        </select>
                    </div>
                </form>
                <button type="submit" onclick="sendCandidate()" class="btn btn-primary">Сохранить</button>
            </div>
        </div>
    </div>
</div>

<script>
    function validate() {
        if ($('#name').val() === '') {
            alert("Заполните: имя кандидата.");
            return false;
        }
        return true;
    }

    function createCandidate() {
        return {
            id:  <%=candidate.getId()%>,
            name: $('#name').val(),
            photo: 11,
            city: $('#city').val()
        }
    }

    function sendCandidate() {
        if (validate()) {
            $.ajax({
                type: 'post',
                url: '<%=request.getContextPath()%>/candidates.do',
                data: createCandidate(),
                dataType: 'text'
            }).done(function (data) {
                document.location.href = '<%=request.getContextPath()%>/candidates.do';
            }).fail(function (err) {
            });
        }
    }
    $(function() {
        getCities();
    });

    function getCities() {
        $.get('<%=request.getContextPath()%>/cities', function(data){
            if(getCityIdByUser() === 0) {
                $("#city").append("<option selected>Выберите город</option>");
            }
            $.each(JSON.parse(data), function(i, city) {
                if(getCityIdByUser() > 0 && getCityIdByUser() === city.id ) {
                    $("#city").append("<option value='"+ city.id +"' selected>" + city.city + "</option>");
                } else {
                    $("#city").append("<option value='"+ city.id +"'>" + city.city + "</option>");
                }
            })
        })
    }

    function getCityIdByUser() {
        return <%=candidate.getCityId()%>;
    }

</script>

</body>
</html>