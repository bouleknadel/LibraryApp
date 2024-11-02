<?php
interface IUserDao
{
    function create($user);
    function findByEmail($email);
}
