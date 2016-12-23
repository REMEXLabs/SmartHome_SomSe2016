<?php

namespace Main\Controller;

use Application\Entity\Heartrate;
use Zend\Mvc\Controller\AbstractRestfulController;

/**
 *
 */
class HeartrateController extends AbstractRestfulController
{
	/**
	 * Return list of resources
	 *
	 * @return array
	 */
	public function getList()
	{
        $heartrateModel = $this->getServiceLocator()->get('HeartrateModel');
        $heartrates = $heartrateModel->getHeartrates();
        return $heartrates;
	}

	/**
	 * Return single resource
	 *
	 * @param mixed $id
	 * @return mixed
	 */
	public function get($id) {
        return array();
    }

	/**
	 * Create a new resource
	 *
	 * @param mixed $data
	 * @return mixed
	 */
	public function create($data) {
        $result = array();

        $value = $data['value'];
        $date = $data['date'];
        $patientId = $data['patientId'];

        $dateTime = null;
        try{
            $dateTime = new \DateTime($date);
        } catch (\Exception $e){
            $result['error'][] = 'invalid date';
        }

        if(is_numeric($value) && $dateTime && is_numeric($patientId)){
            $em = $this->getServiceLocator()->get('Doctrine\ORM\EntityManager');
            $heartrate = new Heartrate($em);
            $heartrate->setValue($value);
            $heartrate->setDate($dateTime);
            $heartrate->setPatientid($patientId);

            $em->persist($heartrate);
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
	public function update($id, $data) {}

	/**
	 * Delete an existing resource
	 *
	 * @param  mixed $id
	 * @return mixed
	 */
	public function delete($id) {}
}
