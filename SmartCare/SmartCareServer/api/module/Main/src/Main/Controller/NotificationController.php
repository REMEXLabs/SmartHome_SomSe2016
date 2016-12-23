<?php

namespace Main\Controller;

use Application\Entity\Notification;
use Application\Entity\Patient;
use Application\Entity\Sleepcycle;
use Zend\Mvc\Controller\AbstractRestfulController;

/**
 *
 */
class NotificationController extends AbstractRestfulController
{
	/**
	 * Return list of resources
	 *
	 * @return array
	 */
	public function getList()
	{
        $notificationModel = $this->getServiceLocator()->get('NotificationModel');
        $result = $notificationModel->getNotifications();
		return $result;
	}

	/**
	 * Return single resource
	 *
	 * @param mixed $id
	 * @return mixed
	 */
	public function get($id) {
        $notificationModel = $this->getServiceLocator()->get('NotificationModel');
        $result = $notificationModel->getNotificationsById($id);
        return $result;
    }

	/**
	 * Create a new resource
	 *
	 * @param mixed $data
	 * @return mixed
	 */
	public function create($data) {
        $result = array();

        $text = $data['text'];
        $patientId = $data['patientId'];
        $date = $data['date'];

        $date = null;
        try{
            $date = new \DateTime($date);
        } catch (\Exception $e){
            $result['error'][] = 'invalid date';
        }

        if($date && is_numeric($patientId)){
            $em = $this->getServiceLocator()->get('Doctrine\ORM\EntityManager');
            $notification = new Notification();
            $notification->setDatetime($date);
            $notification->setText($text);

            $patientModel = $this->getServiceLocator()->get('PatientModel');
            $patient = $patientModel->getPatientByIdObj($patientId);
            $notification->setPatientid($patient);

            $em->persist($notification);
            $em->flush();
            $result['success'] = $data;
        } else  {
            $result['error'] = array(
                'msg' => 'Invalid POST Data',
                'data' => $data,
            );
        }
        return $result;
    }

	/**
	 * Update an existing resource
	 *
	 * @param mixed $id
	 * @param mixed $data
	 * @return mixed
	 */
	public function update($id, $data) {
        return array('not implemented');
    }

	/**
	 * Delete an existing resource
	 *
	 * @param  mixed $id
	 * @return mixed
	 */
	public function delete($id) {
        return array('not implemented');
    }
}
